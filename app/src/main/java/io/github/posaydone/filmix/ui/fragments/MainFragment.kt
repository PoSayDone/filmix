package io.github.posaydone.filmix.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import io.github.posaydone.filmix.data.adapters.CategoriesPagerAdapter
import io.github.posaydone.filmix.data.api.FilmixApiClient
import io.github.posaydone.filmix.data.model.Category
import io.github.posaydone.filmix.data.repository.FilmixRepository
import io.github.posaydone.filmix.databinding.FragmentMainBinding
import io.github.posaydone.filmix.ui.viewmodels.MovieViewModel
import io.github.posaydone.filmix.ui.viewmodels.MovieViewModelFactory


class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private lateinit var adapter: CategoriesPagerAdapter
    private lateinit var viewModel: MovieViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val filmixRepository = FilmixRepository(FilmixApiClient().getApiService(requireContext()))
        val viewModelFactory = MovieViewModelFactory(filmixRepository)
        viewModel =
            ViewModelProvider(requireActivity(), viewModelFactory).get(MovieViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        adapter = CategoriesPagerAdapter(requireParentFragment())
        categoriesViewPager.adapter = adapter

        TabLayoutMediator(categoriesTabLayout, categoriesViewPager) { tab, position ->
            tab.text = when (Category.values()[position]) {
                Category.NEW -> "New"
                Category.POPULAR -> "Popular"
                Category.MOVIES -> "Movies"
                Category.SERIES -> "Series"
                Category.CARTOONS -> "Cartoons"
                Category.ANIMATED_SERIES -> "Animated Series"
                Category.DOCUMENTARY -> "Documentary"
            }
        }.attach()
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}

