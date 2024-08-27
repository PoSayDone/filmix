package io.github.posaydone.filmix.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.posaydone.filmix.data.repository.FilmixRepository

class MovieSearchViewModelFactory(private val repository: FilmixRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieSearchViewModel::class.java)) {
            return MovieSearchViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
