package io.github.posaydone.filmix.presentation.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.posaydone.filmix.data.network.client.FilmixApiClient
import io.github.posaydone.filmix.data.repository.FilmixRepository
import io.github.posaydone.filmix.databinding.FragmentHistoryBinding
import io.github.posaydone.filmix.presentation.ui.adapter.HistoryAdapter
import io.github.posaydone.filmix.presentation.ui.viewModel.HistoryViewModel
import io.github.posaydone.filmix.presentation.ui.viewModel.HistoryViewModelFactory
import io.github.posaydone.filmix.ui.fragment.ShowsSearchFragmentDirections
import io.github.posaydone.filmix.utils.RecyclerViewMargin

class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private val viewModel: HistoryViewModel by viewModels {
        HistoryViewModelFactory(
            FilmixRepository(FilmixApiClient().getApiService(requireContext()))
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(layoutInflater)
        return binding.root
    }

    fun NavController.safeNavigate(direction: NavDirections) {
        currentDestination?.getAction(direction.actionId)?.let {
            navigate(direction)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = HistoryAdapter(listOf()) { item ->
            findNavController().safeNavigate(
                ShowsSearchFragmentDirections.toShowDetailFragment(
                    item.id
                )
            )
        }
        val layoutManager = LinearLayoutManager(requireContext())
        val spanCount = 1
        val decoration = RecyclerViewMargin(spanCount, 48, false)

        historyRecyclerView.layoutManager = layoutManager
        historyRecyclerView.adapter = adapter
        historyRecyclerView.addItemDecoration(decoration)

        viewModel.historyItemsList.observe(viewLifecycleOwner) { itemsList ->
            adapter.updateHistoryItemsList(itemsList)
        }
    }
}