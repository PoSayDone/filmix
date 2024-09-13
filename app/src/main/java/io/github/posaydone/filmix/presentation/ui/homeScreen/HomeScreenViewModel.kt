package io.github.posaydone.filmix.presentation.ui.homeScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.posaydone.filmix.data.entities.FilmixCategory
import io.github.posaydone.filmix.data.entities.Show
import io.github.posaydone.filmix.data.repository.FilmixRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeScreenViewModel(private val filmixRepository: FilmixRepository) : ViewModel() {
    private val _lastSeenShows = MutableStateFlow<List<Show>>(emptyList())
    val lastSeenShows: StateFlow<List<Show>> = _lastSeenShows.asStateFlow()

    private val _viewingShows = MutableStateFlow<List<Show>>(emptyList())
    val viewingShows: StateFlow<List<Show>> = _viewingShows.asStateFlow()

    private val _popularMovies = MutableStateFlow<List<Show>>(emptyList())
    val popularMovies: StateFlow<List<Show>> = _popularMovies.asStateFlow()

    private val _popularSeries = MutableStateFlow<List<Show>>(emptyList())
    val popularSeries: StateFlow<List<Show>> = _popularSeries.asStateFlow()

    private val _popularCartoons = MutableStateFlow<List<Show>>(emptyList())
    val popularCartoons: StateFlow<List<Show>> = _popularCartoons.asStateFlow()

    val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading = _isLoading.asStateFlow()

    val _error = MutableStateFlow<Boolean>(false)
    val error = _error.asStateFlow()

    init {
        loadCategories()
    }

    fun loadCategories() {
        _isLoading.value = true
        _error.value = false

        viewModelScope.launch {
            try {
                val lastSeen = filmixRepository.getLastSeenList(20)
                val viewing = filmixRepository.getViewingShowsList(20)
                val popularMovies =
                    filmixRepository.getPopularShowsList(20, section = FilmixCategory.MOVIE)
                val popularSeries =
                    filmixRepository.getPopularShowsList(20, section = FilmixCategory.SERIES)
                val popularCartoons =
                    filmixRepository.getPopularShowsList(20, section = FilmixCategory.CARTOON)

                _lastSeenShows.value = lastSeen.items
                _viewingShows.value = viewing.items
                _popularMovies.value = popularMovies.items
                _popularSeries.value = popularSeries.items
                _popularCartoons.value = popularCartoons.items
            } catch (e: Exception) {
                _error.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }
}