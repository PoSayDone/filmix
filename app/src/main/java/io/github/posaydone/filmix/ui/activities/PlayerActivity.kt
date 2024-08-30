package io.github.posaydone.filmix.ui.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import io.github.posaydone.filmix.R
import io.github.posaydone.filmix.data.api.RetrofitClient
import io.github.posaydone.filmix.data.db.MainDb
import io.github.posaydone.filmix.data.model.Episode
import io.github.posaydone.filmix.data.model.File
import io.github.posaydone.filmix.data.model.MovieTranslation
import io.github.posaydone.filmix.data.model.Season
import io.github.posaydone.filmix.data.model.Translation
import io.github.posaydone.filmix.data.repository.FilmixRepository
import io.github.posaydone.filmix.data.repository.SeriesProgressRepository
import io.github.posaydone.filmix.databinding.ActivityPlayerBinding
import io.github.posaydone.filmix.databinding.CustomPlayerViewBinding
import io.github.posaydone.filmix.databinding.EpisodesDialogBinding
import io.github.posaydone.filmix.ui.viewmodels.PlayerViewModel
import io.github.posaydone.filmix.ui.viewmodels.PlayerViewModelFactory
import io.github.posaydone.filmix.ui.views.FullscreenBottomSheetDialog

class PlayerActivity : AppCompatActivity() {

    private lateinit var viewModel: PlayerViewModel
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var binding: ActivityPlayerBinding
    private lateinit var controlsBinding: CustomPlayerViewBinding
    private lateinit var episodesDialogBinding: EpisodesDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        makeFullscreen()
        super.onCreate(savedInstanceState)


        val db = MainDb.getDb(this)

        val movieId = intent.getIntExtra("MOVIE_ID", 11)
        val seriesProgressRepository = SeriesProgressRepository(db.getDao())
        val filmixRepository = FilmixRepository(RetrofitClient.apiService)
        val viewModelFactory = PlayerViewModelFactory(filmixRepository, seriesProgressRepository,movieId)

        val episodesDialog = FullscreenBottomSheetDialog(this)
        val episdoesDialogView = LayoutInflater.from(this).inflate(R.layout.episodes_dialog, null)
        episodesDialog.setContentView(episdoesDialogView)

        viewModel = ViewModelProvider(this, viewModelFactory).get(PlayerViewModel::class.java)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        controlsBinding = CustomPlayerViewBinding.bind(binding.root)
        episodesDialogBinding = EpisodesDialogBinding.bind(episdoesDialogView)


        controlsBinding.episodePickerButton.setOnClickListener() {
            episodesDialog.show()
        }
        setContentView(binding.root)

        // Инициализируем ExoPlayer
        setupExoplayer()
        setupSpinners()
        observeViewModel()
    }

    private fun setupExoplayer() {
        exoPlayer = ExoPlayer.Builder(this)
            .build()
            .also { exoPlayer ->
                binding.exoplayerView.player = exoPlayer
            }
    }

    private fun makeFullscreen() {
        val activityInsetsController =
            WindowCompat.getInsetsController(window, window.decorView)
        activityInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }


    private fun setupSpinners() {
        viewModel.contentType.observe(this) { contentType ->
            if (contentType == "movie") {
                controlsBinding.translationSpinner.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            val selectedMovieTranslation =
                                parent.getItemAtPosition(position) as MovieTranslation
                            viewModel.setMovieTranslation(selectedMovieTranslation)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {
                            // Handle case where no item is selected
                        }
                    }
                controlsBinding.qualitySpinner.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            val selectedQuality = parent.getItemAtPosition(position) as File
                            viewModel.setQuality(selectedQuality)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {
                            // Handle case where no item is selected
                        }
                    }
            } else {
                // Setup for season spinner
                episodesDialogBinding.seasonsSpinner.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            val selectedSeason =
                                parent.getItemAtPosition(position) as Season
                            viewModel.setSeason(selectedSeason)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {
                            // Handle case where no item is selected
                        }
                    }

                // Setup for episode spinner
                episodesDialogBinding.episodesSpinner.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            val selectedEpisode =
                                parent.getItemAtPosition(position) as Episode
                            viewModel.setEpisode(selectedEpisode)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {
                            // Handle case where no item is selected
                        }
                    }

                // Setup for translation spinner
                controlsBinding.translationSpinner.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            val selectedTranslation =
                                parent.getItemAtPosition(position) as Translation
                            viewModel.setTranslation(selectedTranslation)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {
                            // Handle case where no item is selected
                        }
                    }

                // Setup for quality spinner
                controlsBinding.qualitySpinner.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            val selectedQuality = parent.getItemAtPosition(position) as File
                            viewModel.setQuality(selectedQuality)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {
                            // Handle case where no item is selected
                        }
                    }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.contentType.observe(this) { contentType ->
            if (contentType == "movie") {
                viewModel.moviePieces.observe(this) { moviePieces ->
                    moviePieces?.let {
                        val moviePiecesAdapter = ArrayAdapter(
                            this,
                            android.R.layout.simple_spinner_item,
                            it
                        )
                        moviePiecesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        controlsBinding.translationSpinner.adapter = moviePiecesAdapter
                    }
                }
                viewModel.selectedMovieTranslation.observe(this) { translation ->
                    translation?.let {
//                val qualities =
//                    listOf(translation.quality) // Or a more complex logic to filter qualities
//                        val qualities = listOf(480)
                        val qualityAdapter = ArrayAdapter(
                            this,
                            android.R.layout.simple_spinner_item,
                            translation.files
                        )
                        qualityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        controlsBinding.qualitySpinner.adapter = qualityAdapter
                    }
                }
            } else {
                viewModel.seasons.observe(this) { seasons ->
                    seasons?.let {
                        val seasonsAdapter = ArrayAdapter(
                            this,
                            android.R.layout.simple_spinner_item,
                            it
                        )
                        seasonsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        episodesDialogBinding.seasonsSpinner.adapter = seasonsAdapter
                    }
                }
                // Observe selected season to update the episode spinner
                viewModel.selectedSeason.observe(this) { season ->
                    season?.let {
                        val episodesAdapter = ArrayAdapter(
                            this,
                            android.R.layout.simple_spinner_item,
                            it.episodes
                        )
                        episodesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        episodesDialogBinding.episodesSpinner.adapter = episodesAdapter
                    }
                }

                // Observe selected episode to update the translation spinner
                viewModel.selectedEpisode.observe(this) { episode ->
                    episode?.let {
                        val translationsAdapter = ArrayAdapter(
                            this,
                            android.R.layout.simple_spinner_item,
                            it.translations
                        )
                        translationsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        controlsBinding.translationSpinner.adapter = translationsAdapter
                    }
                }

                // Observe selected translation to update the quality spinner
                viewModel.selectedTranslation.observe(this) { translation ->
                    translation?.let {
//                val qualities =
//                    listOf(translation.quality) // Or a more complex logic to filter qualities
                        val qualityAdapter = ArrayAdapter(
                            this,
                            android.R.layout.simple_spinner_item,
                            translation.files
                        )
                        qualityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        controlsBinding.qualitySpinner.adapter = qualityAdapter
                    }
                }

            }
            // Observe the video URL and use it to play the video
            viewModel.videoUrl.observe(this) { url ->
                url?.let {
                    playVideo(it)
                }
            }
        }
    }


    private fun playVideo(url: String) {
        val mediaItem = MediaItem.fromUri(url)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
    }
}
