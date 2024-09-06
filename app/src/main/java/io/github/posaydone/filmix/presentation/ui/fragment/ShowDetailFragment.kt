package io.github.posaydone.filmix.presentation.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import io.github.posaydone.filmix.R
import io.github.posaydone.filmix.data.network.client.FilmixApiClient
import io.github.posaydone.filmix.data.network.model.PosterType
import io.github.posaydone.filmix.data.repository.FilmixRepository
import io.github.posaydone.filmix.databinding.FragmentShowDetailBinding
import io.github.posaydone.filmix.presentation.ui.viewModel.ShowDetailViewModel
import io.github.posaydone.filmix.presentation.ui.viewModel.ShowDetailViewModelFactory
import io.github.posaydone.filmix.ui.fragment.ShowDetailFragmentArgs

class ShowDetailFragment : Fragment() {
    private val TAG = "MovieDetailFragment"
    private lateinit var viewModel: ShowDetailViewModel
    private lateinit var binding: FragmentShowDetailBinding
    private var currentPosterType: PosterType? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentShowDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: ShowDetailFragmentArgs by navArgs()
        val showId: Int = args.showId
        val filmixRepository = FilmixRepository(FilmixApiClient().getApiService(requireContext()))
        val viewModelFactory = ShowDetailViewModelFactory(filmixRepository, showId)

        viewModel = ViewModelProvider(this, viewModelFactory).get(ShowDetailViewModel::class.java)

        val config = AppBarConfiguration(findNavController().graph)
        binding.toolbar.setupWithNavController(findNavController(), config)

        viewModel.showDetails.observe(viewLifecycleOwner) { show ->
            with(binding) {
                val ratingFilmix: Double =
                    ((show.votesPos.toDouble()
                        .div((show.votesNeg + show.votesPos).toDouble())) * 10)

                movieTitleTextView.text = show.title
                if (show.originalTitle == "") {
                    movieOriginalTitleTextView.visibility = View.GONE
                } else {
                    movieOriginalTitleTextView.text = show.originalTitle
                }
                movieDescriptionTextView.text = show.shortStory
                kpScoreTextView.text = getString(R.string.score, show.ratingKinopoisk)
                filmixScoreTextView.text = getString(R.string.score, ratingFilmix)
                imdbScoreTextView.text = getString(R.string.score, show.ratingImdb)
                yearTextView.text = show.year.toString()
                countryTextView.text = show.countries.get(0).name
                ageRatingTextView.text = show.mpaa
                if (show.maxEpisode == null) {
                    seasonsCountTextView.visibility = View.GONE
                } else {
                    seasonsCountTextView.text =
                        getString(R.string.seasonsCountString, show.maxEpisode!!.season)
                }
                likeButton.text = show.votesPos.toString()
                dislikeButton.text = show.votesNeg.toString()

                playButton.setOnClickListener {
                    val intent = Intent(
                        requireContext(),
                        io.github.posaydone.filmix.presentation.ui.activity.PlayerActivity::class.java
                    ).apply {
                        putExtra("SHOW_ID", show.id)
                    }
                    startActivity(intent)
                }
            }
        }

        viewModel.showHistory.observe(viewLifecycleOwner) { showHistory ->
            with(binding) {
                if (showHistory.isNullOrEmpty()) {
                    Log.d(TAG, "onViewCreated: Пусто")
                    return@observe
                }
                if (showHistory.first()?.season == 0 && showHistory.first()?.episode == 0) {
                    Log.d(TAG, "onViewCreated: Фильм")
                    playButton.text = getString(R.string.continueWatchingMovie)
                    return@observe
                }
                Log.d(TAG, "onViewCreated: Сериал")
                playButton.text = getString(
                    R.string.continueWatchingSeries,
                    showHistory.first()?.season,
                    showHistory.first()?.episode
                )
            }
        }

        viewModel.showImages.observe(viewLifecycleOwner) { showImages ->
            with(binding) {
                showImages.frames.get(0).let { frame ->
                    Glide.with(root.context).load(frame.url).into(posterImageView)
                    currentPosterType = PosterType.FRAME
                }
                showImages.posters.get(0).let { poster ->
                    if (currentPosterType == null) {
                        Glide.with(root.context).load(poster.url).into(posterImageView)
                        currentPosterType = PosterType.POSTER
                    }
                }
            }
        }

//        viewModel.showTrailers.observe(viewLifecycleOwner) { showTrailers ->
//            showTrailers.get(0).let { trailer ->
//                val file = trailer.files.find { it.quality == 480 }
//                file?.url.let { url ->
//                    with(binding) {
//                        val uri = Uri.parse(url)
//                        posterImageView.visibility = View.GONE
//                        posterVideoView.visibility = View.VISIBLE
//
//                        posterVideoView.setVideoURI(uri);
//                        val mediaController = MediaController(requireContext())
//                        mediaController.setAnchorView(posterVideoView)
//                        posterVideoView.setMediaController(mediaController)
//
//                        posterVideoView.setVideoURI(uri)
//                        posterVideoView.start()
//                    }
//                }
//            }
//        }
    }
}