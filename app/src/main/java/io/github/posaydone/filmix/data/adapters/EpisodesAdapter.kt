package io.github.posaydone.filmix.data.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.posaydone.filmix.R
import io.github.posaydone.filmix.data.model.Episode
import io.github.posaydone.filmix.databinding.ItemEpisodeBinding

class EpisodesAdapter(
    private val onClick: (Episode) -> Unit
) : RecyclerView.Adapter<EpisodesAdapter.EpisodeViewHolder>() {

    private var episodes: List<Episode> = emptyList()

    fun setEpisodes(episodes: List<Episode>) {
        this.episodes = episodes
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_episode, parent, false)
        return EpisodeViewHolder(view)
    }

    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        val episode = episodes[position]
        holder.bind(episode)
        holder.itemView.setOnClickListener { onClick(episode) }
    }

    override fun getItemCount(): Int = episodes.size

    class EpisodeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemEpisodeBinding.bind(view)
        fun bind(episode: Episode) {
            binding.episodeTitleTextView.text = episode.title
        }
    }
}
