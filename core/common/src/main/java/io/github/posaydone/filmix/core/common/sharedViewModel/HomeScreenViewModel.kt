package io.github.posaydone.filmix.core.common.sharedViewModel

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.posaydone.filmix.core.data.FilmixRepository
import io.github.posaydone.filmix.core.data.KinopoiskRepository
import io.github.posaydone.filmix.core.model.FilmixCategory
import io.github.posaydone.filmix.core.model.KinopoiskCountry
import io.github.posaydone.filmix.core.model.KinopoiskGenre
import io.github.posaydone.filmix.core.model.KinopoiskMovie
import io.github.posaydone.filmix.core.model.Rating
import io.github.posaydone.filmix.core.model.SessionManager
import io.github.posaydone.filmix.core.model.Show
import io.github.posaydone.filmix.core.model.ShowDetails
import io.github.posaydone.filmix.core.model.ShowImages
import io.github.posaydone.filmix.core.model.ShowList
import io.github.posaydone.filmix.core.model.Votes
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "HomeScreenViewModel"


data class FeaturedShow(
    val showDetails: ShowDetails,
    val kinopoiskMovie: KinopoiskMovie?,
)

@Immutable
sealed class HomeScreenUiState {
    data object Loading : HomeScreenUiState()
    data class Error(
        val sessionManager: SessionManager,
        val message: String,
        val onRetry: () -> Unit,
    ) : HomeScreenUiState()

    data class Done(
        val sessionManager: SessionManager,
        val featuredShow: FeaturedShow,
        val lastSeenShows: ShowList,
        val viewingShows: ShowList,
        val popularMovies: ShowList,
        val popularSeries: ShowList,
        val popularCartoons: ShowList,
        val getShowImages: suspend (showId: Int) -> ShowImages,
    ) : HomeScreenUiState()
}

@Immutable
sealed interface ImmersiveContentUiState {
    data object Loading : ImmersiveContentUiState
    data class Content(
        val backdropUrl: String?,
        val title: String?,
        val ageRating: Int,
        val logoUrl: String?,
        val description: String?,
        val shortDescription: String?,
        val rating: Rating,
        val votes: Votes,
        val isSeries: Boolean,
        val type: String,
        val seriesLength: Int?,
        val movieLength: Int?,
        val genres: List<KinopoiskGenre>,
        val countries: List<KinopoiskCountry>,
        val year: Int,
    ) : ImmersiveContentUiState

    data object Error : ImmersiveContentUiState
}

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val filmixRepository: FilmixRepository,
    private val kinopoiskRepository: KinopoiskRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {
    private val retryChannel = Channel<Unit>()

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState = retryChannel.receiveAsFlow().flatMapLatest {
        combine(
            filmixRepository.getHistoryList(20).mapToResult(),
            filmixRepository.getViewingList(20).mapToResult(),
            filmixRepository.getPopularList(20, section = FilmixCategory.MOVIE).mapToResult(),
            filmixRepository.getPopularList(20, section = FilmixCategory.SERIES).mapToResult(),
            filmixRepository.getPopularList(20, section = FilmixCategory.CARTOON).mapToResult(),
        ) { lastSeenResult, viewingResult, popularMoviesResult, popularSeriesResult, popularCartoonsResult ->

            val results = listOf(
                lastSeenResult,
                viewingResult,
                popularMoviesResult,
                popularSeriesResult,
                popularCartoonsResult
            )

            val error = results.firstOrNull { it.isFailure }
            if (error != null) {
                HomeScreenUiState.Error(
                    sessionManager = sessionManager,
                    message = error.exceptionOrNull()?.message ?: "Unknown error",
                    onRetry = { retry() })
            } else {
                var show = lastSeenResult.getOrThrow().first()
                var query =
                    if (show.original_name.isNullOrEmpty()) show.title else show.original_name

                val searchResult = kinopoiskRepository.movieSearch(
                    page = 1, limit = 1, query = query
                )
                val kinopoiskMovie = searchResult.docs.firstOrNull()
                val showDetails = filmixRepository.getShowDetails(show.id)

                HomeScreenUiState.Done(
                    sessionManager = sessionManager,
                    featuredShow = FeaturedShow(showDetails, kinopoiskMovie),
                    lastSeenShows = lastSeenResult.getOrThrow(),
                    viewingShows = viewingResult.getOrThrow(),
                    popularMovies = popularMoviesResult.getOrThrow(),
                    popularSeries = popularSeriesResult.getOrThrow(),
                    popularCartoons = popularCartoonsResult.getOrThrow(),
                    getShowImages = { filmixRepository.getShowImages(it) })
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeScreenUiState.Loading
    )

    private val _immersiveContentState =
        MutableStateFlow<ImmersiveContentUiState>(ImmersiveContentUiState.Loading)
    val immersiveContentState: StateFlow<ImmersiveContentUiState> =
        _immersiveContentState.asStateFlow()

    private val kinopoiskCache = mutableMapOf<Int, ImmersiveContentUiState.Content>()
    private var fetchJob: Job? = null

    fun onImmersiveShowFocused(show: Show) {
        fetchJob?.cancel()

        if (kinopoiskCache.containsKey(show.id)) {
            _immersiveContentState.value = kinopoiskCache[show.id]!!
            return
        }

        fetchJob = viewModelScope.launch {
            _immersiveContentState.value = ImmersiveContentUiState.Loading
            try {

                var query = if (show.original_name.isNullOrEmpty()) show.title else show.original_name

                val searchResult = kinopoiskRepository.movieSearch(
                    page = 1, limit = 1, query = query
                )
                val kinopoiskMovie = searchResult.docs.firstOrNull()

                if (kinopoiskMovie != null) {
                    val content = ImmersiveContentUiState.Content(
                        backdropUrl = kinopoiskMovie.backdrop?.url ?: show.poster,
                        title = kinopoiskMovie.name,
                        ageRating = kinopoiskMovie.ageRating,
                        logoUrl = kinopoiskMovie.logo?.url,
                        shortDescription = kinopoiskMovie.shortDescription,
                        description = kinopoiskMovie.description,
                        countries = kinopoiskMovie.countries,
                        type = kinopoiskMovie.type,
                        year = kinopoiskMovie.year,
                        movieLength = kinopoiskMovie.movieLength,
                        seriesLength = kinopoiskMovie.seriesLength,
                        votes = kinopoiskMovie.votes,
                        genres = kinopoiskMovie.genres,
                        rating = kinopoiskMovie.rating,
                        isSeries = kinopoiskMovie.isSeries
                    )

                    kinopoiskCache[show.id] = content
                    _immersiveContentState.value = content
                }

            } catch (e: Exception) {
                _immersiveContentState.value = ImmersiveContentUiState.Error
            }
        }
    }

    init {
        retry()
    }

    fun retry() {
        viewModelScope.launch {
            retryChannel.send(Unit)
        }
    }
}

/**
 * Extension function to map a Flow to a Result.
 */
private fun <T> Flow<T>.mapToResult(): Flow<Result<T>> =
    this.map { Result.success(it) }.catch { emit(Result.failure(it)) }
