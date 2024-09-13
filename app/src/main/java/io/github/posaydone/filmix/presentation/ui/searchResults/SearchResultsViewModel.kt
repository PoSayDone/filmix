package io.github.posaydone.filmix.presentation.ui.searchResults

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import io.github.posaydone.filmix.app.host.Filmix
import io.github.posaydone.filmix.data.entities.Show
import io.github.posaydone.filmix.data.repository.FilmixRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchResultsViewModel(
    repository: FilmixRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val query = checkNotNull(savedStateHandle.get<String>("query"))

    val _shows = MutableStateFlow<List<Show>>(listOf())
    val shows = _shows.asStateFlow()

    val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading = _isLoading.asStateFlow()

    val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()


    init {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val result = repository.getShowsListWithQuery(query)
                _shows.value = result
            } catch (e: Exception) {
                _error.value = "Failed to load movies"
            } finally {
                _isLoading.value = false
            }
        }
    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val savedStateHandle = createSavedStateHandle()
                val filmixRepository = (this[APPLICATION_KEY] as Filmix).filmixRepository
                SearchResultsViewModel(
                    repository = filmixRepository,
                    savedStateHandle = savedStateHandle
                )
            }
        }
    }
}