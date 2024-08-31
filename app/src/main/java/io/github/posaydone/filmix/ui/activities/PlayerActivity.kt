package io.github.posaydone.filmix.ui.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import io.github.posaydone.filmix.data.api.RetrofitClient
import io.github.posaydone.filmix.data.db.MainDb
import io.github.posaydone.filmix.data.repository.FilmixRepository
import io.github.posaydone.filmix.data.repository.SeriesProgressRepository
import io.github.posaydone.filmix.databinding.ActivityPlayerBinding
import io.github.posaydone.filmix.databinding.CustomPlayerViewBinding
import io.github.posaydone.filmix.ui.fragments.EpisodesDialogFragment
import io.github.posaydone.filmix.ui.fragments.PlayerSettingsDialogFragment
import io.github.posaydone.filmix.ui.fragments.TranslationsDialogFragment
import io.github.posaydone.filmix.ui.viewmodels.PlayerViewModel
import io.github.posaydone.filmix.ui.viewmodels.PlayerViewModelFactory

class PlayerActivity : AppCompatActivity() {

    private lateinit var viewModel: PlayerViewModel
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var binding: ActivityPlayerBinding
    private lateinit var controlsBinding: CustomPlayerViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        makeFullscreen()

        super.onCreate(savedInstanceState)

        val db = MainDb.getDb(this)
        val movieId = intent.getIntExtra("MOVIE_ID", 0)

        val seriesProgressRepository = SeriesProgressRepository(db.getDao())
        val filmixRepository = FilmixRepository(RetrofitClient.apiService)
        val viewModelFactory =
            PlayerViewModelFactory(filmixRepository, seriesProgressRepository, movieId)

        val episodesDialogFragment = EpisodesDialogFragment()
        val playerSettingsDialogFragment = PlayerSettingsDialogFragment()
        val translationsDialogFragment = TranslationsDialogFragment()

        viewModel = ViewModelProvider(this, viewModelFactory).get(PlayerViewModel::class.java)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        controlsBinding = CustomPlayerViewBinding.bind(binding.root)

        controlsBinding.playerSettingsButton.setOnClickListener {
            playerSettingsDialogFragment.show(
                supportFragmentManager,
                "fragment_player_settings_dialog"
            )
        }
        controlsBinding.episodePickerButton.setOnClickListener {
            episodesDialogFragment.show(supportFragmentManager, "fragment_episodes_dialog")
        }
        controlsBinding.translationPickerButton.setOnClickListener {
            translationsDialogFragment.show(supportFragmentManager, "fragment_translation_dialog")
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
//                controlsBinding.translationSpinner.onItemSelectedListener =
//                    object : AdapterView.OnItemSelectedListener {
//                        override fun onItemSelected(
//                            parent: AdapterView<*>,
//                            view: View?,
//                            position: Int,
//                            id: Long
//                        ) {
//                            val selectedMovieTranslation =
//                                parent.getItemAtPosition(position) as MovieTranslation
//                            viewModel.setMovieTranslation(selectedMovieTranslation)
//                        }
//
//                        override fun onNothingSelected(parent: AdapterView<*>) {
//                            // Handle case where no item is selected
//                        }
//                    }
//                controlsBinding.qualitySpinner.onItemSelectedListener =
//                    object : AdapterView.OnItemSelectedListener {
//                        override fun onItemSelected(
//                            parent: AdapterView<*>,
//                            view: View?,
//                            position: Int,
//                            id: Long
//                        ) {
//                            val selectedQuality = parent.getItemAtPosition(position) as File
//                            viewModel.setQuality(selectedQuality)
//                        }
//
//                        override fun onNothingSelected(parent: AdapterView<*>) {
//                            // Handle case where no item is selected
//                        }
//                    }
            } else {
//                // Setup for season spinner
//                episodesDialogBinding.seasonsSpinner.onItemSelectedListener =
//                    object : AdapterView.OnItemSelectedListener {
//                        override fun onItemSelected(
//                            parent: AdapterView<*>,
//                            view: View?,
//                            position: Int,
//                            id: Long
//                        ) {
//                            val selectedSeason =
//                                parent.getItemAtPosition(position) as Season
//                            viewModel.setSeason(selectedSeason)
//                        }
//
//                        override fun onNothingSelected(parent: AdapterView<*>) {
//                            // Handle case where no item is selected
//                        }
//                    }
//
//                // Setup for episode spinner
//                episodesDialogBinding.episodesSpinner.onItemSelectedListener =
//                    object : AdapterView.OnItemSelectedListener {
//                        override fun onItemSelected(
//                            parent: AdapterView<*>,
//                            view: View?,
//                            position: Int,
//                            id: Long
//                        ) {
//                            val selectedEpisode =
//                                parent.getItemAtPosition(position) as Episode
//                            viewModel.setEpisode(selectedEpisode)
//                        }
//
//                        override fun onNothingSelected(parent: AdapterView<*>) {
//                            // Handle case where no item is selected
//                        }
//                    }

                // Setup for translation spinner
//                controlsBinding.translationSpinner.onItemSelectedListener =
//                    object : AdapterView.OnItemSelectedListener {
//                        override fun onItemSelected(
//                            parent: AdapterView<*>,
//                            view: View?,
//                            position: Int,
//                            id: Long
//                        ) {
//                            val selectedTranslation =
//                                parent.getItemAtPosition(position) as Translation
//                            viewModel.setTranslation(selectedTranslation)
//                        }
//
//                        override fun onNothingSelected(parent: AdapterView<*>) {
//                            // Handle case where no item is selected
//                        }
//                    }
//
//                // Setup for quality spinner
//                controlsBinding.qualitySpinner.onItemSelectedListener =
//                    object : AdapterView.OnItemSelectedListener {
//                        override fun onItemSelected(
//                            parent: AdapterView<*>,
//                            view: View?,
//                            position: Int,
//                            id: Long
//                        ) {
//                            val selectedQuality = parent.getItemAtPosition(position) as File
//                            viewModel.setQuality(selectedQuality)
//                        }
//
//                        override fun onNothingSelected(parent: AdapterView<*>) {
//                            // Handle case where no item is selected
//                        }
//                    }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.contentType.observe(this) { contentType ->
            if (contentType == "movie") {
//                viewModel.moviePieces.observe(this) { moviePieces ->
//                    moviePieces?.let {
//                        val moviePiecesAdapter = ArrayAdapter(
//                            this,
//                            android.R.layout.simple_spinner_item,
//                            it
//                        )
//                        moviePiecesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                        controlsBinding.translationSpinner.adapter = moviePiecesAdapter
//                    }
//                }
//                viewModel.selectedMovieTranslation.observe(this) { translation ->
//                    translation?.let {
//                        val qualityAdapter = ArrayAdapter(
//                            this,
//                            android.R.layout.simple_spinner_item,
//                            translation.files
//                        )
//                        qualityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                        controlsBinding.qualitySpinner.adapter = qualityAdapter
//                    }
//                }
            } else {
//                viewModel.seasons.observe(this) { seasons ->
//                    seasons?.let {
//                        val seasonPagerAdapter = SeasonPagerAdapter(this, it)
//                        episodesDialogBinding.pager.adapter = seasonPagerAdapter
//                        TabLayoutMediator(
//                            episodesDialogBinding.seasonTabs,
//                            episodesDialogBinding.pager
//                        ) { tab, position ->
//                            tab.text =
//                                getString(
//                                    R.string.season,
//                                    viewModel.seasons.value!![position].season
//                                )
//                        }.attach()
//
//                    }
//                }
//                viewModel.selectedSeason.observe(this) {selectedSeason ->
//                    Objects.requireNonNull(episodesDialogBinding.seasonTabs.getTabAt(selectedSeason?.season!!.minus(1)))
//                        ?.select();
//                }

//                // Observe selected episode to update the translation spinner
//                viewModel.selectedEpisode.observe(this) { episode ->
//                    episode?.let {
//                        val translationsAdapter = ArrayAdapter(
//                            this,
//                            android.R.layout.simple_spinner_item,
//                            it.translations
//                        )
//                        translationsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                        controlsBinding.translationSpinner.adapter = translationsAdapter
//                    }
//                }
//
//                // Observe selected translation to update the quality spinner
//                viewModel.selectedTranslation.observe(this) { translation ->
//                    translation?.let {
//                        val qualityAdapter = ArrayAdapter(
//                            this,
//                            android.R.layout.simple_spinner_item,
//                            translation.files
//                        )
//                        qualityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                        controlsBinding.qualitySpinner.adapter = qualityAdapter
//                    }
//                }
            }

            viewModel.details.observe(this) { details ->
                controlsBinding.showTitleTextView.text = details.title
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
