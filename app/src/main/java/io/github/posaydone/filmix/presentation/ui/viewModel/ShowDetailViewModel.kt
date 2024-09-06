package io.github.posaydone.filmix.presentation.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.posaydone.filmix.data.network.model.ShowDetails
import io.github.posaydone.filmix.data.network.model.ShowHistoryItem
import io.github.posaydone.filmix.data.network.model.ShowImages
import io.github.posaydone.filmix.data.network.model.ShowTrailers
import io.github.posaydone.filmix.data.repository.FilmixRepository
import kotlinx.coroutines.launch

class ShowDetailViewModel(private val repository: FilmixRepository, movieId: Int) : ViewModel() {

    private val _showDetails = MutableLiveData<ShowDetails>()
    val showDetails: LiveData<ShowDetails> get() = _showDetails

    private val _showImages = MutableLiveData<ShowImages>()
    val showImages: LiveData<ShowImages> get() = _showImages

    private val _showTrailers = MutableLiveData<ShowTrailers>()
    val showTrailers: LiveData<ShowTrailers> get() = _showTrailers

    private val _showHistory = MutableLiveData<List<ShowHistoryItem?>>()
    val showHistory: LiveData<List<ShowHistoryItem?>> get() = _showHistory

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    init {
        _error.value = null

        viewModelScope.launch {
            try {
                val movie = repository.getShowDetails(movieId)
                val images = repository.getShowImages(movieId)
                val trailers = repository.getShowTrailers(movieId)
                val history = repository.getShowHistory(movieId)
                _showDetails.value = movie
                _showImages.value = images
                _showTrailers.value = trailers
                _showHistory.value = history
            } catch (e: Exception) {
                _error.value = "Failed to load movie"
            }
        }
    }
}
