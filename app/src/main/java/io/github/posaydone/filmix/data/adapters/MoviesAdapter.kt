package io.github.posaydone.filmix.data.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.github.posaydone.filmix.R
import io.github.posaydone.filmix.data.model.MovieCard
import io.github.posaydone.filmix.databinding.MovieCardBinding

class MoviesAdapter(
    private var movieCards: List<MovieCard>,
    private val onItemClick: (MovieCard) -> Unit
) : RecyclerView.Adapter<MoviesAdapter.MovieViewHolder>() {

    class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = MovieCardBinding.bind(view)
        fun bind(item: MovieCard) = with(binding) {
            movieTitle.text = item.title
            Glide.with(binding.root.context).load(item.poster).into(binding.movieImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_card, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movieCards[position])
        holder.itemView.setOnClickListener { onItemClick(movieCards[position]) }
    }

    override fun getItemCount(): Int = movieCards.size

    fun updateMovies(newMovieCards: List<MovieCard>) {
        movieCards = newMovieCards
        notifyDataSetChanged()
    }
}
