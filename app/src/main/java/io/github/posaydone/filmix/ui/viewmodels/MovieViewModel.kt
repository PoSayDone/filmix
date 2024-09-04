package io.github.posaydone.filmix.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.posaydone.filmix.data.model.Category
import io.github.posaydone.filmix.data.model.ShowsPage
import io.github.posaydone.filmix.data.repository.FilmixRepository
import kotlinx.coroutines.launch


class MovieViewModel(private val repository: FilmixRepository) : ViewModel() {

    private val _categoryData = mutableMapOf<Category, MutableLiveData<ShowsPage>>()

    init {
        Category.values().forEach { category ->
            _categoryData[category] = MutableLiveData()
        }
    }

    fun getCategoryData(category: Category): LiveData<ShowsPage> {
        if (_categoryData[category]?.value == null) {
            loadCategoryData(category)
        }
        return _categoryData[category]!!
    }

    private fun loadCategoryData(category: Category) {
        viewModelScope.launch {
            val showsPage = when (category) {
                Category.NEW -> repository.fetchNew()
                Category.POPULAR -> repository.fetchPopular()
                Category.MOVIES -> repository.fetchList(category = "s0")
                Category.SERIES -> repository.fetchList(category = "s7")
                Category.CARTOONS -> repository.fetchList(category = "s14")
                Category.ANIMATED_SERIES -> repository.fetchList(category = "s93")
                Category.DOCUMENTARY -> repository.fetchList(category = "s0", genre = "g15")
            }
            _categoryData[category]?.postValue(showsPage)
        }
    }
//    fun loadMoreMovies(page: Int) {
//        viewModelScope.launch {
//            val movieList = repository.fetchList(24, page)
//            _categories.value = _categories.value?.plus(movieList)
//        }
//    }
}