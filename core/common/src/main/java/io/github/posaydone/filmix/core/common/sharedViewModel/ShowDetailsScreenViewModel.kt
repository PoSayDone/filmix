package io.github.posaydone.filmix.core.common.sharedViewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.posaydone.filmix.core.data.FilmixRepository
import io.github.posaydone.filmix.core.model.SessionManager
import io.github.posaydone.filmix.core.model.ShowDetails
import io.github.posaydone.filmix.core.model.ShowImages
import io.github.posaydone.filmix.core.model.ShowProgress
import io.github.posaydone.filmix.core.model.ShowTrailers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed class ShowDetailsScreenUiState {
    data object Loading : ShowDetailsScreenUiState()
    data class Error(val message: String, val onRetry: () -> Unit) : ShowDetailsScreenUiState()
    data class Done(
        val sessionManager: SessionManager,
        val showDetails: ShowDetails,
        val showImages: ShowImages,
        val showTrailers: ShowTrailers,
        val showProgress: ShowProgress,
    ) : ShowDetailsScreenUiState()
}

@HiltViewModel
class ShowDetailsScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: FilmixRepository,
    sessionManager: SessionManager,
) : ViewModel() {
    private val retryChannel = Channel<Unit>(Channel.RENDEZVOUS)

    val uiState = retryChannel.receiveAsFlow().flatMapLatest {
        savedStateHandle.getStateFlow<Int?>("showId", null).map { showId ->
            if (showId == null) {
                ShowDetailsScreenUiState.Error(
                    message = "Show id is invalid",
                    onRetry = { retry() })
            } else {
                try {
                    val details = repository.getShowDetails(showId)
                    val images = repository.getShowImages(showId)
                    val trailers = repository.getShowTrailers(showId)
                    val history = repository.getShowProgress(showId)
                    ShowDetailsScreenUiState.Done(
                        showDetails = details,
                        showImages = images,
                        showTrailers = trailers,
                        showProgress = history,
                        sessionManager = sessionManager
                    )
                } catch (error: Exception) {
                    ShowDetailsScreenUiState.Error(message = "Unknown error", onRetry = { retry() })
                }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ShowDetailsScreenUiState.Loading
    )

    init {
        retry()
    }

    private fun retry() {
        viewModelScope.launch {
            retryChannel.send(Unit)
        }
    }
}
