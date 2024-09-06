package io.github.posaydone.filmix.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.github.posaydone.filmix.R
import io.github.posaydone.filmix.data.network.model.ShowCard
import io.github.posaydone.filmix.databinding.ShowCardBinding

class ShowCardListAdapter(
    private var showCards: List<ShowCard>,
    private val onItemClick: (ShowCard) -> Unit
) : RecyclerView.Adapter<ShowCardListAdapter.ShowCardViewHolder>() {

    class ShowCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ShowCardBinding.bind(view)
        fun bind(item: ShowCard) = with(binding) {
            titleTextView.text = item.title
            Glide.with(binding.root.context).load(item.poster).into(binding.posterImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowCardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.show_card, parent, false)
        return ShowCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShowCardViewHolder, position: Int) {
        holder.bind(showCards[position])
        holder.itemView.setOnClickListener { onItemClick(showCards[position]) }
    }

    override fun getItemCount(): Int = showCards.size

    fun updateShows(newShowCards: List<ShowCard>) {
        showCards = newShowCards
        notifyDataSetChanged()
    }
}
