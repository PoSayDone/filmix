package io.github.posaydone.filmix.presentation.ui.showDetailsScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import io.github.posaydone.filmix.app.host.Filmix
import io.github.posaydone.filmix.data.entities.ShowDetails
import io.github.posaydone.filmix.data.entities.ShowHistoryItem
import io.github.posaydone.filmix.data.entities.ShowImages
import io.github.posaydone.filmix.data.entities.ShowTrailers
import io.github.posaydone.filmix.data.repository.FilmixRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ShowDetailsScreenViewModel(
    private val repository: FilmixRepository,
    savedStateHandle: SavedStateHandle,
) :
    ViewModel() {

    private val showId = checkNotNull(savedStateHandle.get<Int>("showId"))

    private val _showDetails = MutableStateFlow<ShowDetails?>(null)
    val showDetails: StateFlow<ShowDetails?> get() = _showDetails

    private val _showImages = MutableStateFlow<ShowImages?>(null)
    val showImages: StateFlow<ShowImages?> get() = _showImages

    private val _showTrailers = MutableStateFlow<ShowTrailers?>(null)
    val showTrailers: StateFlow<ShowTrailers?> get() = _showTrailers

    private val _showHistory = MutableStateFlow<List<ShowHistoryItem?>>(listOf())
    val showHistory: StateFlow<List<ShowHistoryItem?>> get() = _showHistory

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    init {
        viewModelScope.launch {
            try {
                val movie = repository.getShowDetails(showId)
                val images = repository.getShowImages(showId)
                val trailers = repository.getShowTrailers(showId)
                val history = repository.getShowHistory(showId)
                _showDetails.value = movie
                _showImages.value = images
                _showTrailers.value = trailers
                _showHistory.value = history
            } catch (e: Exception) {
                _error.value = "Failed to load movie"
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val savedStateHandle = createSavedStateHandle()
                val filmixRepository = (this[APPLICATION_KEY] as Filmix).filmixRepository
                ShowDetailsScreenViewModel(
                    repository = filmixRepository,
                    savedStateHandle = savedStateHandle
                )
            }
        }
    }
}
