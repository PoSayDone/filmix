package io.github.posaydone.filmix.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.posaydone.filmix.data.model.MovieCard
import io.github.posaydone.filmix.data.repository.FilmixRepository
import kotlinx.coroutines.launch


class MovieViewModel(private val repository: FilmixRepository) : ViewModel() {

    private var _movies = MutableLiveData<List<MovieCard>>()
    val movies: LiveData<List<MovieCard>> get() = _movies

    init {
        viewModelScope.launch {
            val movieList = repository.fetchList(24)
            _movies.value = movieList
        }
    }

    fun loadMoreMovies(page: Int) {
        viewModelScope.launch {
            val movieList = repository.fetchList(24, page)
            _movies.value = _movies.value?.plus(movieList)
        }
    }
}
