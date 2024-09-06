package io.github.posaydone.filmix.presentation.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import io.github.posaydone.filmix.data.network.model.Category
import io.github.posaydone.filmix.data.network.model.ShowCard
import io.github.posaydone.filmix.databinding.FragmentCategoryShowsListBinding
import io.github.posaydone.filmix.presentation.ui.adapter.ShowCardListAdapter
import io.github.posaydone.filmix.presentation.ui.viewModel.CategoriesViewModel
import io.github.posaydone.filmix.utils.RecyclerViewMargin

class CategoryShowsListFragment : Fragment() {

    private val viewModel: CategoriesViewModel by activityViewModels()
    private lateinit var category: Category
    private lateinit var binding: FragmentCategoryShowsListBinding
    private lateinit var adapter: ShowCardListAdapter
    private var showCards: List<ShowCard> = listOf()

    companion object {
        private const val ARG_CATEGORY = "category"

        fun newInstance(category: Category): CategoryShowsListFragment {
            val fragment = CategoryShowsListFragment()
            val args = Bundle()
            args.putSerializable(ARG_CATEGORY, category)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        category = arguments?.getSerializable(ARG_CATEGORY) as Category
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryShowsListBinding.inflate(layoutInflater)
        return binding.root
    }

    fun NavController.safeNavigate(direction: NavDirections) {
        currentDestination?.getAction(direction.actionId)?.let {
            navigate(direction)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        val spanCount = 3
        val gridLayoutManager = GridLayoutManager(activity, spanCount)
        rcMovies.layoutManager = gridLayoutManager
        val decoration = RecyclerViewMargin(spanCount, 24, false)
        rcMovies.addItemDecoration(decoration)

        adapter = ShowCardListAdapter(showCards) { show ->
            findNavController().safeNavigate(
                ShowsSearchFragmentDirections.toShowDetailFragment(
                    show.id
                )
            )
        }
        rcMovies.adapter = adapter

        viewModel.getCategoryData(category).observe(viewLifecycleOwner) { showsPage ->
            adapter.updateShows(showsPage.items)
        }
    }
}