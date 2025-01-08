package io.github.posaydone.filmix.core.common.sharedViewModel

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.posaydone.filmix.core.data.FilmixRepository
import io.github.posaydone.filmix.core.model.FilmixCategory
import io.github.posaydone.filmix.core.model.ShowList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "HomeScreenViewModel"

@Immutable
sealed class HomeScreenUiState {
    data object Loading : HomeScreenUiState()
    data class Error(val message: String, val onRetry: () -> Unit) : HomeScreenUiState()
    data class Done(
        val lastSeenShows: ShowList,
        val viewingShows: ShowList,
        val popularMovies: ShowList,
        val popularSeries: ShowList,
        val popularCartoons: ShowList,
    ) : HomeScreenUiState()
}

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val filmixRepository: FilmixRepository,
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
                    message = error.exceptionOrNull()?.message ?: "Unknown error",
                    onRetry = { retry() }
                )
            } else {
                HomeScreenUiState.Done(
                    lastSeenShows = lastSeenResult.getOrThrow(),
                    viewingShows = viewingResult.getOrThrow(),
                    popularMovies = popularMoviesResult.getOrThrow(),
                    popularSeries = popularSeriesResult.getOrThrow(),
                    popularCartoons = popularCartoonsResult.getOrThrow(),
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeScreenUiState.Loading
    )

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
    this.map { Result.success(it) }
        .catch { emit(Result.failure(it)) }
