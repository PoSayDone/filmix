package io.github.posaydone.filmix.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.github.posaydone.filmix.R
import io.github.posaydone.filmix.data.network.model.ShowDetails
import io.github.posaydone.filmix.databinding.ItemHistoryBinding

class HistoryAdapter(
    private var historyItemsList: List<ShowDetails>,
    private val onItemClick: (ShowDetails) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.HistoryItemViewHolder>() {

    class HistoryItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val TAG = "HistoryItemViewHolder"
        val binding = ItemHistoryBinding.bind(view)
        fun bind(item: ShowDetails) = with(binding) {
            titleTextView.text = item.title
            desciprtionTextView.text = item.shortStory
            Glide.with(binding.root.context).load(item.poster).into(binding.posterImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return HistoryItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryItemViewHolder, position: Int) {
        holder.bind(historyItemsList[position])
        holder.itemView.setOnClickListener { onItemClick(historyItemsList[position]) }
    }

    override fun getItemCount(): Int = historyItemsList.size

    fun updateHistoryItemsList(newHistoryItemsList: List<ShowDetails>) {
        historyItemsList = newHistoryItemsList
        notifyDataSetChanged()
    }
}
