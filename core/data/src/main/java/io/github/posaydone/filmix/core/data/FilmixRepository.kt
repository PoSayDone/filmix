package io.github.posaydone.filmix.core.data

import io.github.posaydone.filmix.core.model.FilmixCategory
import io.github.posaydone.filmix.core.model.PageWithShows
import io.github.posaydone.filmix.core.model.Show
import io.github.posaydone.filmix.core.model.ShowDetails
import io.github.posaydone.filmix.core.model.ShowImages
import io.github.posaydone.filmix.core.model.ShowList
import io.github.posaydone.filmix.core.model.ShowProgress
import io.github.posaydone.filmix.core.model.ShowProgressItem
import io.github.posaydone.filmix.core.model.ShowResourceResponse
import io.github.posaydone.filmix.core.model.ShowTrailers
import io.github.posaydone.filmix.core.network.FilmixRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FilmixRepository @Inject constructor(private val filmixRemoteDataSource: FilmixRemoteDataSource) {
    suspend fun getPage(
        limit: Int = 48,
        page: Int? = null,
        category: String = "s0",
        genre: String? = null,
    ): PageWithShows<Show> {
        return filmixRemoteDataSource.fetchPage(
            category = category, page = page, limit = limit, genre = genre
        )
    }

    fun getList(
        limit: Int = 48,
        page: Int? = null,
        category: String = "s0",
        genre: String? = null,
    ): Flow<ShowList> = flow {
        val list = getPage(
            category = category, page = page, limit = limit, genre = genre
        ).items
        emit(list)
    }

    suspend fun getViewingPage(limit: Int = 48, page: Int = 1): PageWithShows<Show> {
        return filmixRemoteDataSource.fetchViewingPage(
            page = page, limit = limit
        )
    }

    fun getViewingList(limit: Int = 48, page: Int = 1): Flow<ShowList> = flow {
        val list = getViewingPage(
            page = page, limit = limit
        ).items
        emit(list)
    }

    suspend fun getPopularPage(
        limit: Int = 48,
        page: Int? = null,
        section: FilmixCategory = FilmixCategory.MOVIE,
    ): PageWithShows<Show> {
        return filmixRemoteDataSource.fetchPopularPage(
            limit = limit, page = page, section = section
        )
    }

    fun getPopularList(
        limit: Int = 48,
        page: Int? = null,
        section: FilmixCategory = FilmixCategory.MOVIE,
    ): Flow<ShowList> = flow {
        val list = getPopularPage(
            limit = limit, page = page, section = section
        ).items
        emit(list)
    }

    suspend fun getHistoryPageFull(
        limit: Int = 10,
        page: Int? = null,
    ): PageWithShows<ShowDetails> {
        return filmixRemoteDataSource.fetchHistoryPageFull(
            limit = limit,
            page = page,
        )
    }

    fun getHistoryListFull(
        limit: Int = 10,
        page: Int? = null,
    ): Flow<List<ShowDetails>> = flow {
        val list = getHistoryPageFull(
            limit = limit,
            page = page,
        ).items
        emit(list)
    }

    suspend fun getHistoryPage(limit: Int = 10, page: Int = 1): PageWithShows<Show> {
        return filmixRemoteDataSource.fetchHistoryPage(
            limit = limit, page = page
        )
    }

    fun getHistoryList(limit: Int = 10, page: Int = 1): Flow<ShowList> = flow {
        val list = getHistoryPage(
            limit = limit, page = page
        ).items
        emit(list)
    }

    // Поиск фильмов по запросу
    suspend fun getShowsListWithQuery(query: String, limit: Int = 48): List<Show> {
        return filmixRemoteDataSource.fetchShowsListWithQuery(query, limit)
    }

    // Получение деталей фильма, включая сезоны, серии и озвучки
    suspend fun getShowDetails(movieId: Int): ShowDetails {
        return filmixRemoteDataSource.fetchShowDetails(movieId)
    }

    suspend fun getShowImages(movieId: Int): ShowImages {
        return filmixRemoteDataSource.fetchShowImages(movieId)
    }

    suspend fun getShowTrailers(movieId: Int): ShowTrailers {
        return filmixRemoteDataSource.fetchShowTrailers(movieId)
    }

    suspend fun getShowProgress(movieId: Int): ShowProgress {
        return filmixRemoteDataSource.fetchShowProgress(movieId)
    }

    suspend fun addShowProgress(movieId: Int, showProgressItem: ShowProgressItem) {
        filmixRemoteDataSource.addShowProgress(movieId, showProgressItem)
    }

    // Получение ссылки на видео для выбранного сезона, серии и озвучки
    suspend fun getShowResource(movieId: Int): ShowResourceResponse {
        return filmixRemoteDataSource.fetchShowResource(movieId)
    }

    suspend fun toggleFavorite(showId: Int, isFavorite: Boolean): Boolean {
        return if (isFavorite) {
            filmixRemoteDataSource.addToFavorites(showId)
        } else {
            filmixRemoteDataSource.removeFromFavorites(showId)
        }
    }
}