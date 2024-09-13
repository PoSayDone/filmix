package io.github.posaydone.filmix.presentation.ui.historyScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.posaydone.filmix.data.entities.ShowDetails
import io.github.posaydone.filmix.data.repository.FilmixRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HistoryScreenViewModel(private val repository: FilmixRepository) : ViewModel() {

    private val TAG: String = "HistoryViewModel"
    private val _historyItemsList = MutableStateFlow<List<ShowDetails>>(listOf())
    val historyItemsList: StateFlow<List<ShowDetails>> get() = _historyItemsList

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    init {
        viewModelScope.launch {
            try {
                val result = repository.getLastSeenListFull()
                _historyItemsList.value = result.items
            } catch (e: Exception) {
                _error.value = "Failed to load movies"
            }
        }
    }
}
