package io.github.posaydone.filmix.presentation.ui.showDetailsScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.posaydone.filmix.core.data.FilmixRepository
import io.github.posaydone.filmix.core.model.ShowDetails
import io.github.posaydone.filmix.core.model.ShowImages
import io.github.posaydone.filmix.core.model.ShowProgress
import io.github.posaydone.filmix.core.model.ShowTrailers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


sealed class ShowDetailsScreenUiState {
    data object Loading : ShowDetailsScreenUiState()
    data object Error : ShowDetailsScreenUiState()
    data class Done(
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
) : ViewModel() {
    val uiState = savedStateHandle
        .getStateFlow<Int?>("showId", null)
        .map { showId ->
            if (showId == null) {
                ShowDetailsScreenUiState.Error
            } else {
                val details = repository.getShowDetails(showId)
                val images = repository.getShowImages(showId)
                val trailers = repository.getShowTrailers(showId)
                val history = repository.getShowProgress(showId)
                ShowDetailsScreenUiState.Done(
                    showDetails = details,
                    showImages = images,
                    showTrailers = trailers,
                    showProgress = history,

                    )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ShowDetailsScreenUiState.Loading
        )
}
