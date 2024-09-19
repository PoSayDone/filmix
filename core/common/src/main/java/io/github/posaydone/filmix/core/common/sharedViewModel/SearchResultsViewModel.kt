package io.github.posaydone.filmix.core.common.sharedViewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.posaydone.filmix.core.data.FilmixRepository
import io.github.posaydone.filmix.core.model.Show
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchResultsViewModel @Inject constructor(
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
}