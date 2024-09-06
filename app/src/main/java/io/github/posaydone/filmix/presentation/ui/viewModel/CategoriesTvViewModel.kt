package io.github.posaydone.filmix.presentation.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.posaydone.filmix.data.network.model.ShowCard
import io.github.posaydone.filmix.data.repository.FilmixRepository
import kotlinx.coroutines.launch

class CategoriesTvViewModel(private val filmixRepository: FilmixRepository) : ViewModel() {

    // LiveData for each movie list
    private val _lastSeenMovies = MutableLiveData<List<ShowCard>>()
    val lastSeenMovies: LiveData<List<ShowCard>> get() = _lastSeenMovies

    private val _popularMovies = MutableLiveData<List<ShowCard>>()
    val popularMovies: LiveData<List<ShowCard>> get() = _popularMovies

    private val _newMovies = MutableLiveData<List<ShowCard>>()
    val newMovies: LiveData<List<ShowCard>> get() = _newMovies

    init {
        viewModelScope.launch {
            val lastSeen = filmixRepository.getLastSeenList()
            val popular = filmixRepository.getPopularShowsList()
            val new = filmixRepository.getNewShowsList()

            _lastSeenMovies.value = lastSeen.items
            _popularMovies.value = popular.items
            _newMovies.value = new.items
        }
    }
}