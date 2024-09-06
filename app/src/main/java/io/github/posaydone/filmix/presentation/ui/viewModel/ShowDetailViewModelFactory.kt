package io.github.posaydone.filmix.presentation.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.posaydone.filmix.data.repository.FilmixRepository

class ShowDetailViewModelFactory(
    private val repository: FilmixRepository,
    private val showId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShowDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ShowDetailViewModel(repository, showId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
