package io.github.posaydone.filmix.presentation.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import io.github.posaydone.filmix.data.network.client.FilmixApiClient
import io.github.posaydone.filmix.data.repository.FilmixRepository
import io.github.posaydone.filmix.databinding.FragmentShowsSearchBinding
import io.github.posaydone.filmix.presentation.ui.adapter.SearchAdapter
import io.github.posaydone.filmix.presentation.ui.viewModel.ShowsSearchViewModel
import io.github.posaydone.filmix.presentation.ui.viewModel.ShowsSearchViewModelFactory
import io.github.posaydone.filmix.utils.RecyclerViewMargin

class ShowsSearchFragment : Fragment() {

    private var _binding: FragmentShowsSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ShowsSearchViewModel by viewModels {
        ShowsSearchViewModelFactory(
            FilmixRepository(FilmixApiClient().getApiService(requireContext()))
        )
    }

    private lateinit var adapter: SearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShowsSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
//        binding.searchView.setupWithSearchBar(binding.searchBar)
        binding.searchView.editText.doOnTextChanged { text, _, _, _ ->
            if (text.toString() != "") {
                performSearch(text.toString())
            }
        }
        binding.searchView.onFocusChangeListener
    }

    private fun performSearch(query: String) {
        viewModel.searchMovies(query)
    }

    fun NavController.safeNavigate(direction: NavDirections) {
        currentDestination?.getAction(direction.actionId)?.let {
            navigate(direction)
        }
    }

    private fun setupRecyclerView() = with(binding) {
        adapter = SearchAdapter(emptyList()) { show ->
            findNavController().safeNavigate(
                ShowsSearchFragmentDirections.toShowDetailFragment(
                    show.id
                )
            )
        }

        val spanCount = 3
        searchResultsRecyclerView.layoutManager = GridLayoutManager(requireContext(), spanCount)
        val decoration = RecyclerViewMargin(spanCount, 24, false)
        searchResultsRecyclerView.addItemDecoration(decoration)
        searchResultsRecyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.movies.observe(viewLifecycleOwner) { movies ->
            adapter.updateShows(movies)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
