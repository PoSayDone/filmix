package io.github.posaydone.filmix.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.posaydone.filmix.data.model.MovieCard
import io.github.posaydone.filmix.data.repository.FilmixRepository
import kotlinx.coroutines.launch


class MovieViewModel(private val repository: FilmixRepository) : ViewModel() {

    private var cachedMovieCards: List<MovieCard>? = null

    private val _movies = MutableLiveData<List<MovieCard>>()
    val movies: LiveData<List<MovieCard>> get() = _movies

    fun loadMovies() {
        viewModelScope.launch {
            // Проверяем, есть ли уже закэшированные фильмы
            if (cachedMovieCards == null) {
                // Если нет, загружаем их из репозитория
                val movieList = repository.fetchMovies()
                cachedMovieCards = movieList // Кэшируем данные
                _movies.value = movieList
            } else {
                // Если есть, используем кэшированные данные
                _movies.value = cachedMovieCards!!
            }
        }
    }
}
