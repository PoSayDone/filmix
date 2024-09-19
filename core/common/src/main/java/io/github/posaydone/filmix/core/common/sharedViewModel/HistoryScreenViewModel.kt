package io.github.posaydone.filmix.core.common.sharedViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.posaydone.filmix.core.data.FilmixRepository
import io.github.posaydone.filmix.core.model.ShowDetails
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed class HistoryScreenUiState {
    data object Loading : HistoryScreenUiState()
    data object Error : HistoryScreenUiState()
    data class Done(
        val historyList: List<ShowDetails>,
    ) : HistoryScreenUiState()
}

@HiltViewModel
class HistoryScreenViewModel @Inject constructor(private val repository: FilmixRepository) :
    ViewModel() {

    val uiState = repository.getHistoryListFull().map { list ->
        HistoryScreenUiState.Done(historyList = list)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HistoryScreenUiState.Loading
    )
}
