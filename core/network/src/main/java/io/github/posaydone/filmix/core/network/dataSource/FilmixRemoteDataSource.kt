package io.github.posaydone.filmix.core.network.dataSource

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import io.github.posaydone.filmix.core.model.FilmixCategory
import io.github.posaydone.filmix.core.model.FilmixSeries
import io.github.posaydone.filmix.core.model.PageWithShows
import io.github.posaydone.filmix.core.model.Show
import io.github.posaydone.filmix.core.model.ShowDetails
import io.github.posaydone.filmix.core.model.ShowImages
import io.github.posaydone.filmix.core.model.ShowProgress
import io.github.posaydone.filmix.core.model.ShowProgressItem
import io.github.posaydone.filmix.core.model.ShowResourceResponse
import io.github.posaydone.filmix.core.model.ShowTrailers
import io.github.posaydone.filmix.core.model.VideoWithQualities
import io.github.posaydone.filmix.core.network.service.FilmixApiService
import io.github.posaydone.filmix.core.network.utils.transformSeries
import javax.inject.Inject

class FilmixRemoteDataSource @Inject constructor(
    private val filmixApiService: FilmixApiService,
) {
    suspend fun fetchPage(
        limit: Int = 48,
        page: Int? = null,
        category: String = "s0",
        genre: String? = null,
    ): PageWithShows<Show> {
        return filmixApiService.fetchPage(
            category = category, page = page, limit = limit, genre = genre
        )
    }

    suspend fun fetchViewingPage(limit: Int = 48, page: Int = 1): PageWithShows<Show> {
        return filmixApiService.fetchViewingPage(
            page = page, limit = limit
        )
    }

    suspend fun fetchPopularPage(
        limit: Int = 48,
        page: Int? = null,
        section: FilmixCategory = FilmixCategory.MOVIE,
    ): PageWithShows<Show> {
        return filmixApiService.fetchPopularPage(
            limit = limit, page = page, section = section
        )
    }

    suspend fun fetchHistoryPageFull(
        limit: Int = 10,
        page: Int? = null,
    ): PageWithShows<ShowDetails> {
        return filmixApiService.fetchHistoryPageFull(
            limit = limit,
            page = page,
        )
    }

    suspend fun fetchHistoryPage(limit: Int = 10, page: Int = 1): PageWithShows<Show> {
        return filmixApiService.fetchHistoryPage(
            limit = limit, page = page
        )
    }

    // Поиск фильмов по запросу
    suspend fun fetchShowsListWithQuery(query: String, limit: Int = 48): List<Show> {
        return filmixApiService.fetchShowsListWithQuery(query, limit).items
    }

    // Получение деталей фильма, включая сезоны, серии и озвучки
    suspend fun fetchShowDetails(movieId: Int): ShowDetails {
        return filmixApiService.fetchShowDetails(movieId)
    }

    suspend fun fetchShowImages(movieId: Int): ShowImages {
        return filmixApiService.fetchShowImages(movieId)
    }

    suspend fun fetchShowTrailers(movieId: Int): ShowTrailers {
        return filmixApiService.fetchShowTrailers(movieId)
    }

    suspend fun fetchShowProgress(movieId: Int): ShowProgress {
        return filmixApiService.fetchShowProgress(movieId)
    }

    suspend fun addShowProgress(movieId: Int, showProgressItem: ShowProgressItem) {
        filmixApiService.addShowProgress(movieId, showProgressItem)
    }

    // Получение ссылки на видео для выбранного сезона, серии и озвучки
    suspend fun fetchShowResource(movieId: Int): ShowResourceResponse {
        val response = filmixApiService.fetchShowResource(movieId)

        if (response.isSuccessful) {
            val responseBody = response.body()?.string() ?: ""

            val gson = Gson()

            return try {
                val filmixSeriesType = object : TypeToken<FilmixSeries>() {}.type
                val filmixSeries = gson.fromJson<FilmixSeries>(responseBody, filmixSeriesType)
                val seriesTransformed = transformSeries(filmixSeries)
                ShowResourceResponse.SeriesResourceResponse(seriesTransformed)
            } catch (e: JsonSyntaxException) {
                try {
                    val moviesType = object : TypeToken<List<VideoWithQualities>>() {}.type
                    val movies = gson.fromJson<List<VideoWithQualities>>(responseBody, moviesType)
                    ShowResourceResponse.MovieResourceResponse(movies)
                } catch (e: JsonSyntaxException) {
                    throw IllegalStateException("Unexpected response format")
                }
            }
        } else {
            throw Exception("HTTP error: ${response.code()}")
        }
    }


    suspend fun addToFavorites(showId: Int): Boolean {
        return try {
            val response = filmixApiService.addToFavorites(showId)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun fetchFavoritesPage(
        limit: Int = 48,
        page: Int? = null,
    ): PageWithShows<Show> {
        return filmixApiService.fetchFavoritesPage(
            page = page,
            limit = limit,
        )
    }

    suspend fun removeFromFavorites(showId: Int): Boolean {
        return try {
            val response = filmixApiService.removeFromFavorites(showId)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}