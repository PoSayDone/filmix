package io.github.posaydone.filmix.presentation.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import io.github.posaydone.filmix.app.host.Filmix
import io.github.posaydone.filmix.presentation.ui.historyScreen.HistoryScreenViewModel
import io.github.posaydone.filmix.presentation.ui.homeScreen.HomeScreenViewModel

@Suppress("UNCHECKED_CAST")
val FilmixViewModelFactory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T =
        with(modelClass) {
            val application = checkNotNull(extras[APPLICATION_KEY])
            val filmixRepository = (application as Filmix).filmixRepository
            when {
                isAssignableFrom(HomeScreenViewModel::class.java) -> HomeScreenViewModel(
                    filmixRepository
                )

                isAssignableFrom(HistoryScreenViewModel::class.java) -> HistoryScreenViewModel(
                    filmixRepository
                )

                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}
