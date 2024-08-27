package io.github.posaydone.filmix.data.repository

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import io.github.posaydone.filmix.data.api.FilmixApiService
import io.github.posaydone.filmix.data.model.MovieCard
import io.github.posaydone.filmix.data.model.MovieDetails
import io.github.posaydone.filmix.data.model.MoviePiece
import io.github.posaydone.filmix.data.model.Series
import io.github.posaydone.filmix.data.model.MovieOrSeriesResponse

class FilmixRepository(private val apiService: FilmixApiService) {

    // Получение списка фильмов
    suspend fun fetchMovies(): List<MovieCard> {
        return apiService.getMovies().items
    }

    // Поиск фильмов по запросу
    suspend fun searchMovies(query: String): List<MovieCard> {
        return apiService.searchMovies(query)
    }

    // Получение деталей фильма, включая сезоны, серии и озвучки
    suspend fun fetchMovieDetails(movieId: Int): MovieDetails {
        return apiService.getMovieDetails(movieId)
    }

    // Получение ссылки на видео для выбранного сезона, серии и озвучки
    suspend fun fetchSeriesOrMovie(movieId: Int): MovieOrSeriesResponse {
        val response = apiService.getSeriesOrMovie(movieId)

        if (response.isSuccessful) {
            val responseBody = response.body()?.string() ?: ""

            val gson = Gson()

            return try {
                val seriesType = object : TypeToken<Series>() {}.type
                val series = gson.fromJson<Series>(responseBody, seriesType)
                MovieOrSeriesResponse.SeriesResponse(series)
            } catch (e: JsonSyntaxException) {
                try {
                    val moviesType = object : TypeToken<List<MoviePiece>>() {}.type
                    val movies = gson.fromJson<List<MoviePiece>>(responseBody, moviesType)
                    MovieOrSeriesResponse.MovieResponse(movies)
                } catch (e: JsonSyntaxException) {
                    throw IllegalStateException("Unexpected response format")
                }
            }
        } else {
            throw Exception("HTTP error: ${response.code()}")
        }
    }
}
