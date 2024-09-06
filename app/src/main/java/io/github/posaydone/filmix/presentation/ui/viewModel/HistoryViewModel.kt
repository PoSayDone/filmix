package io.github.posaydone.filmix.presentation.ui.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.posaydone.filmix.data.network.model.ShowDetails
import io.github.posaydone.filmix.data.repository.FilmixRepository
import kotlinx.coroutines.launch

class HistoryViewModel(private val repository: FilmixRepository) : ViewModel() {

    private val TAG: String = "HistoryViewModel"
    private val _historyItemsList = MutableLiveData<List<ShowDetails>>()
    val historyItemsList: LiveData<List<ShowDetails>> get() = _historyItemsList

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    init {
        viewModelScope.launch {
            try {
                val result = repository.getLastSeenListFull()
                Log.d(TAG, "${result.items}")
                _historyItemsList.value = result.items
            } catch (e: Exception) {
                Log.d(TAG, "Items not fetched")
                _error.value = "Failed to load movies"
            }
        }
    }
}
