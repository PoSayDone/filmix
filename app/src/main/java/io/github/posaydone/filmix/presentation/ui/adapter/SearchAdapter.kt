package io.github.posaydone.filmix.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.github.posaydone.filmix.R
import io.github.posaydone.filmix.data.network.model.ShowCard
import io.github.posaydone.filmix.databinding.ShowCardBinding

class SearchAdapter(
    private var shows: List<ShowCard>,
    private val onItemClick: (ShowCard) -> Unit
) : RecyclerView.Adapter<SearchAdapter.Holder>() {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ShowCardBinding.bind(view)
        fun bind(item: ShowCard) = with(binding) {
            titleTextView.text = item.title
            Glide.with(binding.root.context).load(item.poster).into(binding.posterImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.show_card, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(shows[position])
        holder.itemView.setOnClickListener { onItemClick(shows[position]) }
    }

    override fun getItemCount(): Int = shows.size

    fun updateShows(newShows: List<ShowCard>) {
        shows = newShows
        notifyDataSetChanged()
    }
}
