package io.github.posaydone.filmix.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.posaydone.filmix.R
import io.github.posaydone.filmix.data.adapters.EpisodesAdapter
import io.github.posaydone.filmix.data.model.Episode
import io.github.posaydone.filmix.data.model.Season
import io.github.posaydone.filmix.ui.viewmodels.PlayerViewModel
import io.github.posaydone.filmix.utils.OnEpisodeClickListener

class EpisodesListFragment : Fragment(), OnEpisodeClickListener {

    private val TAG = "EpisodeList"
    private lateinit var season: Season
    private lateinit var sharedViewModel: PlayerViewModel

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

        sharedViewModel = ViewModelProvider(requireActivity()).get(PlayerViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_episodes_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = EpisodesAdapter(season, season.episodes, sharedViewModel)
    }

    override fun onEpisodeClick(episode: Episode) {
        Log.d(TAG, "onEpisodeClick: ${episode}")
        sharedViewModel.setEpisode(episode)
    }
}
