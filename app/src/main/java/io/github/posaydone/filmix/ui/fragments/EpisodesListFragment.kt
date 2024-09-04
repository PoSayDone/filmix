package io.github.posaydone.filmix.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.posaydone.filmix.data.adapters.EpisodesAdapter
import io.github.posaydone.filmix.data.model.Episode
import io.github.posaydone.filmix.data.model.Season
import io.github.posaydone.filmix.databinding.FragmentEpisodesListBinding
import io.github.posaydone.filmix.ui.viewmodels.PlayerViewModel
import io.github.posaydone.filmix.utils.OnEpisodeClickListener

class EpisodesListFragment : Fragment(), OnEpisodeClickListener {

    private val TAG = "EpisodeList"
    private lateinit var season: Season
    private val viewModel: PlayerViewModel by activityViewModels()
    private lateinit var binding: FragmentEpisodesListBinding

    companion object {
        private const val ARG_SEASON = "season"

        fun newInstance(season: Season): EpisodesListFragment {
            val fragment = EpisodesListFragment()
            val args = Bundle().apply {
                putParcelable(ARG_SEASON, season)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            season = it.getParcelable(ARG_SEASON)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentEpisodesListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = EpisodesAdapter(season, season.episodes, viewModel)
    }

    override fun onEpisodeClick(episode: Episode) {
        viewModel.setEpisode(episode)
    }
}
