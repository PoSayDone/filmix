package io.github.posaydone.filmix.ui.fragments

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
import io.github.posaydone.filmix.data.adapters.ShowCardListAdapter
import io.github.posaydone.filmix.data.model.Category
import io.github.posaydone.filmix.data.model.ShowCard
import io.github.posaydone.filmix.databinding.FragmentShowsListBinding
import io.github.posaydone.filmix.ui.viewmodels.MovieViewModel
import io.github.posaydone.filmix.utils.RecyclerViewMargin

class ShowsListFragment : Fragment() {

    private val viewModel: MovieViewModel by activityViewModels()
    private lateinit var category: Category
    private lateinit var binding: FragmentShowsListBinding
    private lateinit var adapter: ShowCardListAdapter
    private var showCards: List<ShowCard> = listOf()

    companion object {
        private const val ARG_CATEGORY = "category"

        fun newInstance(category: Category): ShowsListFragment {
            val fragment = ShowsListFragment()
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
        binding = FragmentShowsListBinding.inflate(layoutInflater)
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

        adapter = ShowCardListAdapter(showCards) { movie ->
            findNavController().safeNavigate(
                MovieSearchFragmentDirections.toMovieDetailFragment(
                    movie.id
                )
            )
        }
        rcMovies.adapter = adapter
        viewModel.getCategoryData(category).observe(viewLifecycleOwner) { showsPage ->
            adapter.updateShows(showsPage.items)
        }

    }

//    override fun onEpisodeClick(episode: Episode) {
//        sharedViewModel.setEpisode(episode)
//    }
}
