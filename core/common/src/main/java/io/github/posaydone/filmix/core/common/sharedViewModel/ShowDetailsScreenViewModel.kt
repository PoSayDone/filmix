package io.github.posaydone.filmix.core.common.sharedViewModel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.posaydone.filmix.core.data.repository.FilmixRepository
import io.github.posaydone.filmix.core.data.repository.KinopoiskRepository
import io.github.posaydone.filmix.core.model.KinopoiskMovie
import io.github.posaydone.filmix.core.model.SessionManager
import io.github.posaydone.filmix.core.model.ShowDetails
import io.github.posaydone.filmix.core.model.ShowImages
import io.github.posaydone.filmix.core.model.ShowProgress
import io.github.posaydone.filmix.core.model.ShowTrailers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


sealed class ShowDetailsScreenUiState {
    data object Loading : ShowDetailsScreenUiState()
    data class Error(val message: String, val onRetry: () -> Unit) : ShowDetailsScreenUiState()
    data class Done(
        val sessionManager: SessionManager,
        val kinopoiskMovie: KinopoiskMovie?,
        val showDetails: ShowDetails,
        val showImages: ShowImages,
        val showTrailers: ShowTrailers,
        val showProgress: ShowProgress,
        val toggleFavorites: () -> Unit,
    ) : ShowDetailsScreenUiState()
}

private var TAG = "SWAG"

@HiltViewModel
class ShowDetailsScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val filmixRepository: FilmixRepository,
    private val kinopoiskRepository: KinopoiskRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {
    private val retryChannel = Channel<Unit>(Channel.RENDEZVOUS)

    val uiState = retryChannel.receiveAsFlow().flatMapLatest {
        savedStateHandle.getStateFlow<Int?>("showId", null).map { showId ->
            if (showId == null) {
                ShowDetailsScreenUiState.Error(
                    message = "Show id is invalid", onRetry = { reload() })
            } else {
                try {
                    val details = filmixRepository.getShowDetails(showId)
                    val images = filmixRepository.getShowImages(showId)
                    val trailers = filmixRepository.getShowTrailers(showId)
                    val history = filmixRepository.getShowProgress(showId)

                    var query =
                        if (details.originalTitle.isNullOrEmpty()) details.title else details.originalTitle

                    val searchResult = kinopoiskRepository.movieSearch(
                        page = 1, limit = 1, query = query
                    )
                    Log.d(TAG, "searchresult: $searchResult")

                    val kinopoiskMovie = searchResult.docs.firstOrNull()

                    Log.d(TAG, "kinopoiskmovie: $kinopoiskMovie")

                    ShowDetailsScreenUiState.Done(
                        showDetails = details,
                        showImages = images,
                        showTrailers = trailers,
                        showProgress = history,
                        kinopoiskMovie = kinopoiskMovie,
                        sessionManager = sessionManager,
                        toggleFavorites = { toggleFavorites() }
                    )
                } catch (error: Exception) {
                    ShowDetailsScreenUiState.Error(
                        message = "Unknown error",
                        onRetry = { reload() })
                }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ShowDetailsScreenUiState.Loading
    )


    init {
        reload()
    }

    fun toggleFavorites() {
        viewModelScope.launch {
            val currentState = uiState.value
            if (currentState is ShowDetailsScreenUiState.Done) {
                val currentShowDetails = currentState.showDetails
                val newFavoriteState = !(currentShowDetails.isFavorite ?: false)

                val success = withContext(Dispatchers.IO) {
                    filmixRepository.toggleFavorite(
                        showId = currentShowDetails.id,
                        isFavorite = newFavoriteState
                    )
                }

                if (success) {
                    reload()
                }
            }
        }
    }

    fun reload() {
        viewModelScope.launch {
            retryChannel.send(Unit)
        }
    }
}
