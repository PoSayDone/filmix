package io.github.posaydone.filmix.presentation.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.posaydone.filmix.data.network.model.Category
import io.github.posaydone.filmix.data.network.model.ShowsPage
import io.github.posaydone.filmix.data.repository.FilmixRepository
import kotlinx.coroutines.launch


class CategoriesViewModel(private val repository: FilmixRepository) : ViewModel() {

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
                Category.NEW -> repository.getNewShowsList()
                Category.POPULAR -> repository.getPopularShowsList()
                Category.MOVIES -> repository.getShowsList(category = "s0")
                Category.SERIES -> repository.getShowsList(category = "s7")
                Category.CARTOONS -> repository.getShowsList(category = "s14")
                Category.ANIMATED_SERIES -> repository.getShowsList(category = "s93")
                Category.DOCUMENTARY -> repository.getShowsList(category = "s0", genre = "g15")
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