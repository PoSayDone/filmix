package io.github.posaydone.filmix.data.repository

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import io.github.posaydone.filmix.data.entities.Episode
import io.github.posaydone.filmix.data.entities.File
import io.github.posaydone.filmix.data.entities.FilmixCategory
import io.github.posaydone.filmix.data.entities.FilmixSeries
import io.github.posaydone.filmix.data.entities.LastSeenPage
import io.github.posaydone.filmix.data.entities.Season
import io.github.posaydone.filmix.data.entities.Series
import io.github.posaydone.filmix.data.entities.Show
import io.github.posaydone.filmix.data.entities.ShowDetails
import io.github.posaydone.filmix.data.entities.ShowHistoryItem
import io.github.posaydone.filmix.data.entities.ShowImages
import io.github.posaydone.filmix.data.entities.ShowResponse
import io.github.posaydone.filmix.data.entities.ShowTrailers
import io.github.posaydone.filmix.data.entities.ShowsPage
import io.github.posaydone.filmix.data.entities.Translation
import io.github.posaydone.filmix.data.entities.VideoWithQualities
import io.github.posaydone.filmix.data.network.service.FilmixApiService

class FilmixRepository(private val apiService: FilmixApiService) {
    private val TAG: String = "FilmixRepo"

    suspend fun getShowHistory(movieId: Int): List<ShowHistoryItem?> {
        return apiService.fetchShowHistory(movieId)
    }

    suspend fun setShowHistory(movieId: Int, seriesHistory: ShowHistoryItem) {
        return apiService.setShowHistory(movieId, seriesHistory)
    }

    // Получение списка фильмов
    suspend fun getShowsList(
        limit: Int = 48,
        page: Int? = null,
        category: String = "s0",
        genre: String? = null,
    ): ShowsPage {
        return apiService.getList(
            category = category,
            page = page,
            limit = limit,
            genre = genre
        )
    }

    suspend fun getViewingShowsList(limit: Int = 48, page: Int = 1): ShowsPage {
        return apiService.fetchViewingShowsList(
            page = page,
            limit = limit
        )
    }

    suspend fun getPopularShowsList(
        limit: Int = 48,
        page: Int = 1,
        section: FilmixCategory = FilmixCategory.MOVIE,
    ): ShowsPage {
        return apiService.fetchPopularShowsList(
            page = page,
            section = section,
            limit = limit
        )
    }

    suspend fun getLastSeenListFull(
        limit: Int = 10,
        page: Int = 1,
        full: Boolean? = true,
    ): LastSeenPage {
        return apiService.fetchLastSeenListFull(
            page = page,
            limit = limit,
            full = full
        )
    }

    suspend fun getLastSeenList(limit: Int = 10, page: Int = 1): ShowsPage {
        return apiService.fetchLastSeenList(
            page = page,
            limit = limit,
        )
    }

    // Поиск фильмов по запросу
    suspend fun getShowsListWithQuery(query: String, limit: Int = 48): List<Show> {
        return apiService.fetchShowsListWithQuery(query, limit).items
    }

    // Получение деталей фильма, включая сезоны, серии и озвучки
    suspend fun getShowDetails(movieId: Int): ShowDetails {
        return apiService.fetchShowDetails(movieId)
    }

    suspend fun getShowImages(movieId: Int): ShowImages {
        return apiService.fetchShowImages(movieId)
    }

    suspend fun getShowTrailers(movieId: Int): ShowTrailers {
        return apiService.fetchShowTrailers(movieId)
    }

    // Получение ссылки на видео для выбранного сезона, серии и озвучки
    suspend fun getShow(movieId: Int): ShowResponse {
        val response = apiService.fetchShow(movieId)

        if (response.isSuccessful) {
            val responseBody = response.body()?.string() ?: ""

            val gson = Gson()

            return try {
                val filmixSeriesType = object : TypeToken<FilmixSeries>() {}.type
                val filmixSeries = gson.fromJson<FilmixSeries>(responseBody, filmixSeriesType)
                val seriesTransformed = transformSeries(filmixSeries)
                ShowResponse.SeriesResponse(seriesTransformed)
            } catch (e: JsonSyntaxException) {
                try {
                    val moviesType = object : TypeToken<List<VideoWithQualities>>() {}.type
                    val movies = gson.fromJson<List<VideoWithQualities>>(responseBody, moviesType)
                    ShowResponse.MovieResponse(movies)
                } catch (e: JsonSyntaxException) {
                    throw IllegalStateException("Unexpected response format")
                }
            }
        } else {
            throw Exception("HTTP error: ${response.code()}")
        }
    }

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
}
