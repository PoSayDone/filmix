package io.github.posaydone.filmix.presentation.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.posaydone.filmix.data.network.model.ShowCard
import io.github.posaydone.filmix.data.repository.FilmixRepository
import kotlinx.coroutines.launch

class ShowsSearchViewModel(private val repository: FilmixRepository) : ViewModel() {

    private val _movies = MutableLiveData<List<ShowCard>>()
    val movies: LiveData<List<ShowCard>> get() = _movies

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun searchMovies(query: String) {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val result = repository.getShowsListWithQuery(query)
                _movies.value = result
            } catch (e: Exception) {
                _error.value = "Failed to load movies"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
