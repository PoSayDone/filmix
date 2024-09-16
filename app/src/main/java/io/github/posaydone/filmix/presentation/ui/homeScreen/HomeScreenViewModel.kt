package io.github.posaydone.filmix.presentation.ui.homeScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.posaydone.filmix.core.data.FilmixRepository
import io.github.posaydone.filmix.core.model.FilmixCategory
import io.github.posaydone.filmix.core.model.ShowList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

private const val TAG = "HomeScreenViewModel"

sealed class HomeScreenUiState {
    data object Loading : HomeScreenUiState()
    data object Error : HomeScreenUiState()
    data class Done(
        val lastSeenShows: ShowList,
        val viewingShows: ShowList,
        val popularMovies: ShowList,
        val popularSeries: ShowList,
        val popularCartoons: ShowList,
    ) : HomeScreenUiState()
}

@HiltViewModel
class HomeScreenViewModel @Inject constructor(filmixRepository: FilmixRepository) : ViewModel() {
    val uiState = combine(
        filmixRepository.getHistoryList(20),
        filmixRepository.getViewingList(20),
        filmixRepository.getPopularList(20, section = FilmixCategory.MOVIE),
        filmixRepository.getPopularList(20, section = FilmixCategory.SERIES),
        filmixRepository.getPopularList(20, section = FilmixCategory.CARTOON),
    ) { (lastSeenShows, viewingShows, popularMovies, popularSeries, popularCartoons) ->
        HomeScreenUiState.Done(
            lastSeenShows = lastSeenShows,
            viewingShows = viewingShows,
            popularMovies = popularMovies,
            popularSeries = popularSeries,
            popularCartoons = popularCartoons,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeScreenUiState.Loading
    )

}