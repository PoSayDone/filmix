package io.github.posaydone.filmix.presentation.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.posaydone.filmix.data.repository.FilmixRepository
import io.github.posaydone.filmix.data.repository.SeriesProgressRepository

class PlayerViewModelFactory(
    private val repository: FilmixRepository,
    private val seriesProgressRepository: SeriesProgressRepository,
    private val movieId: Int
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
            return PlayerViewModel(repository, seriesProgressRepository, movieId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
