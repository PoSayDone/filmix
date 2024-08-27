package io.github.posaydone.filmix.ui.fragments

import android.R.attr.numColumns
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import io.github.posaydone.filmix.data.adapters.MoviesAdapter
import io.github.posaydone.filmix.data.api.RetrofitClient
import io.github.posaydone.filmix.data.repository.FilmixRepository
import io.github.posaydone.filmix.databinding.FragmentMovieListBinding
import io.github.posaydone.filmix.ui.viewmodels.MovieViewModel
import io.github.posaydone.filmix.ui.viewmodels.MovieViewModelFactory
import io.github.posaydone.filmix.utils.RecyclerViewMargin


class MovieListFragment : Fragment() {
    private lateinit var binding: FragmentMovieListBinding
    private lateinit var moviesAdapter: MoviesAdapter

    private val movieViewModel: MovieViewModel by viewModels {
        MovieViewModelFactory(FilmixRepository(RetrofitClient.apiService))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMovieListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()
    }

    private fun initRcView() = with(binding) {
        rcMovies.layoutManager = GridLayoutManager(activity, 3)
        val decoration = RecyclerViewMargin(24, numColumns)
        rcMovies.addItemDecoration(decoration)

        moviesAdapter = MoviesAdapter(emptyList()) { movie ->
            val action = MovieListFragmentDirections.actionMovieSearchFragmentToMovieDetailFragment(movie.id)
            findNavController().navigate(action)
        }
        rcMovies.adapter = moviesAdapter
        movieViewModel.movies.observe(viewLifecycleOwner, Observer { movies ->
            moviesAdapter.updateMovies(movies)
        })

        // Запуск загрузки фильмов
        movieViewModel.loadMovies()
    }

    companion object {
        @JvmStatic
        fun newInstance() = MovieListFragment()
    }
}

