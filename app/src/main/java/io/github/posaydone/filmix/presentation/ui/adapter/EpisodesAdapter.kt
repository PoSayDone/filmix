package io.github.posaydone.filmix.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.github.posaydone.filmix.R
import io.github.posaydone.filmix.data.network.model.Episode
import io.github.posaydone.filmix.data.network.model.Season
import io.github.posaydone.filmix.presentation.ui.viewModel.PlayerViewModel

class EpisodesAdapter(
    private val season: Season,
    private val episodes: List<Episode>,
    private val viewModel: PlayerViewModel
) :
    RecyclerView.Adapter<EpisodesAdapter.EpisodeViewHolder>() {
    private var selectedEpisode: Episode? = null

    init {
        // Observe the selected episode in the ViewModel
        viewModel.selectedEpisode.observeForever { episode ->
            val previousSelected = selectedEpisode
            selectedEpisode = episode

            // Update UI when the selected episode changes
            previousSelected?.let { notifyItemChanged(episodes.indexOf(it)) }
            episode?.let { notifyItemChanged(episodes.indexOf(it)) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_episode, parent, false)
        return EpisodeViewHolder(view)
    }

    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        val episode = episodes[position]
        holder.bind(episode)
        // Highlight the selected item
        holder.itemView.isSelected = (episode == selectedEpisode)

        holder.itemView.setOnClickListener {
            viewModel.setSeason(season)
            viewModel.setEpisode(episode)
        }
    }

    override fun getItemCount(): Int = episodes.size

    class EpisodeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(episode: Episode) {
            (itemView as TextView).text = episode.toString()
        }
    }
}
