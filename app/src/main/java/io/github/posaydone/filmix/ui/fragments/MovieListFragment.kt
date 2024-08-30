package io.github.posaydone.filmix.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    private var isLoading = true
    private var page = 2

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
        val spanCount = 3
        val gridLayoutManager = GridLayoutManager(activity, spanCount)
        rcMovies.layoutManager = gridLayoutManager
        val decoration = RecyclerViewMargin(spanCount, 24, false)
        rcMovies.addItemDecoration(decoration)

        moviesAdapter = MoviesAdapter(emptyList()) { movie ->
            val action = MovieListFragmentDirections.actionMovieSearchFragmentToMovieDetailFragment(movie.id)
            findNavController().navigate(action)
        }
        rcMovies.adapter = moviesAdapter
        movieViewModel.movies.observe(viewLifecycleOwner, Observer { movies ->
            moviesAdapter.updateMovies(movies)
        })

        rcMovies.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    super.onScrolled(recyclerView, dx, dy)
                    val visibleItemCount = gridLayoutManager.childCount
                    val totalItemCount = gridLayoutManager.itemCount
                    val firstVisibleItemPosition = gridLayoutManager.findFirstVisibleItemPosition()

                    if (isLoading) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount) {
                            isLoading = false;
                            movieViewModel.loadMoreMovies(page)
                            page++
                            isLoading = true;
                        }
                    }
                }
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() = MovieListFragment()
    }
}

