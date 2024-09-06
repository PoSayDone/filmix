package io.github.posaydone.filmix.presentation.ui.presenter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import io.github.posaydone.filmix.R
import io.github.posaydone.filmix.data.network.model.ShowCard

class ShowPresenter : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.show_card_tv, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any?) {
        val movie = item as ShowCard
        val imageView = viewHolder.view.findViewById<ImageView>(R.id.posterImageView)
        val titleView = viewHolder.view.findViewById<TextView>(R.id.titleTextView)

        titleView.text = movie.title

        // Load movie image (e.g., with Glide)
        Glide.with(viewHolder.view.context)
            .load(movie.poster)
            .into(imageView)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        // Clean up if necessary
    }
}
