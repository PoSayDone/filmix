package io.github.posaydone.filmix.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.posaydone.filmix.data.model.MovieDetails
import io.github.posaydone.filmix.data.repository.FilmixRepository
import kotlinx.coroutines.launch

class MovieDetailViewModel(private val repository: FilmixRepository) : ViewModel() {

    private val _movieDetails = MutableLiveData<MovieDetails>()
    val movieDetails: LiveData<MovieDetails> get() = _movieDetails

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error
    
    fun loadMovieDetails(movieId: Int) {
        _error.value = null
        
        viewModelScope.launch {
            try {
                val movie = repository.fetchMovieDetails(movieId)
                _movieDetails.postValue(movie)
            } catch (e: Exception) {
                _error.value = "Failed to load movie"
            }
        }
    }
}
