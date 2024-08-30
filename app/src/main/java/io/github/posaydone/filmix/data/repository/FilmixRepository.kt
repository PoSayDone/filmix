package io.github.posaydone.filmix.data.repository

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import io.github.posaydone.filmix.data.api.FilmixApiService
import io.github.posaydone.filmix.data.model.Episode
import io.github.posaydone.filmix.data.model.File
import io.github.posaydone.filmix.data.model.FilmixSeries
import io.github.posaydone.filmix.data.model.MovieCard
import io.github.posaydone.filmix.data.model.MovieDetails
import io.github.posaydone.filmix.data.model.MovieOrSeriesResponse
import io.github.posaydone.filmix.data.model.MovieTranslation
import io.github.posaydone.filmix.data.model.Season
import io.github.posaydone.filmix.data.model.Series
import io.github.posaydone.filmix.data.model.Translation

class FilmixRepository(private val apiService: FilmixApiService) {
    private val TAG: String = "FilmixRepo"

    fun transformSeries(filmixSeries: FilmixSeries): Series {
        val transformedSeasons = mutableListOf<Season>()

        filmixSeries.forEach { (translationKey, translationValue) ->
            translationValue.forEach { (_, seasonValue) ->
                var season = transformedSeasons.find { it.season == seasonValue.season }
                if (season == null) {
                    season = Season(
                        seasonValue.season,
                        mutableListOf()
                    )
                    transformedSeasons.add(season)
                }
                seasonValue.episodes.forEach { (_, episodeValue) ->
                    var episode = season.episodes.find { it.episode == episodeValue.episode }
                    if (episode == null) {
                        episode = Episode(
                            episodeValue.episode,
                            episodeValue.ad_skip,
                            episodeValue.title,
                            episodeValue.released,
                            mutableListOf()
                        )
                        season.episodes.add(episode)
                    }
                    val tranlationWithFiles = Translation(
                        translationKey,
                        episodeValue.files.map { file ->
                            File(
                                url = file.url,
                                quality = file.quality,
                                proPlus = file.proPlus
                            )
                        }
                    )
                    episode.translations.add(tranlationWithFiles)
                }
            }
        }

        return Series(seasons = transformedSeasons)
    }

    // Получение списка фильмов
    suspend fun fetchList(count: Int = 48, page: Int = 1): List<MovieCard> {
        return apiService.getList(
            "",
            "s7",
            page,
            count
        ).items
    }

    // Поиск фильмов по запросу
    suspend fun fetchListWIthQuerry(query: String): List<MovieCard> {
        return apiService.getList(query, "").items
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
                val filmixSeriesType = object : TypeToken<FilmixSeries>() {}.type
                val filmixSeries = gson.fromJson<FilmixSeries>(responseBody, filmixSeriesType)
                val seriesTransformed = transformSeries(filmixSeries)
                MovieOrSeriesResponse.SeriesResponse(seriesTransformed)
            } catch (e: JsonSyntaxException) {
                try {
                    val moviesType = object : TypeToken<List<MovieTranslation>>() {}.type
                    val movies = gson.fromJson<List<MovieTranslation>>(responseBody, moviesType)
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
