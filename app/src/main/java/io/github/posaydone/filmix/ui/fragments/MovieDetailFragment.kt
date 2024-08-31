package io.github.posaydone.filmix.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import io.github.posaydone.filmix.R
import io.github.posaydone.filmix.data.api.RetrofitClient
import io.github.posaydone.filmix.data.repository.FilmixRepository
import io.github.posaydone.filmix.databinding.FragmentMovieDetailBinding
import io.github.posaydone.filmix.ui.activities.PlayerActivity
import io.github.posaydone.filmix.ui.viewmodels.MovieDetailViewModel
import io.github.posaydone.filmix.ui.viewmodels.MovieDetailViewModelFactory
import kotlin.math.round

class MovieDetailFragment : Fragment() {
    private lateinit var viewModel: MovieDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMovieDetailBinding.inflate(inflater, container, false)

        val args: MovieDetailFragmentArgs by navArgs()
        val movieId: Int = args.id

        val repository = FilmixRepository(RetrofitClient.apiService)
        val viewModelFactory = MovieDetailViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MovieDetailViewModel::class.java)

        // Наблюдение за изменениями данных фильма
        viewModel.movieDetails.observe(viewLifecycleOwner) { movie ->
            // Обновляем UI
            binding.movieTitleTextView.text = movie.title
            if (movie.original_title == "") {
                binding.movieOriginalTitleTextView.visibility = View.GONE
            } else {
                binding.movieOriginalTitleTextView.text = movie.original_title
            }
            binding.movieDescriptionTextView.text = movie.short_story
            binding.kpScoreTextView.text = movie.ratingKinopoisk.toString()
            binding.imdbScoreTextView.text = movie.ratingImdb.toString()
            binding.filmixScoreTextView.text =
                round((movie.votesPos / (movie.votesPos + movie.votesNeg)) * 10.0).toString()
            binding.yearTextView.text = movie.year.toString()
            binding.countryTextView.text = movie.countries.get(0).name
            binding.ageRatingTextView.text = movie.mpaa
            binding.seasonsCountTextView.text =
                getString(R.string.seasonsCountString, movie.max_episode.season)
            binding.likeButton.text = movie.votesPos.toString()
            binding.dislikeButton.text = movie.votesNeg.toString()
            Glide.with(binding.root.context).load(movie.poster).into(binding.moviePosterImageView)

            binding.playButton.setOnClickListener {
                val intent = Intent(requireContext(), PlayerActivity::class.java).apply {
                    putExtra("MOVIE_ID", movie.id)
                }
                startActivity(intent)
            }
        }

        // Загрузка данных фильма
        viewModel.loadMovieDetails(movieId)

        return binding.root
    }
}
