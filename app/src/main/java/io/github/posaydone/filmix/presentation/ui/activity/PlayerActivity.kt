package io.github.posaydone.filmix.presentation.ui.activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import io.github.posaydone.filmix.R
import io.github.posaydone.filmix.data.db.MainDb
import io.github.posaydone.filmix.data.network.client.FilmixApiClient
import io.github.posaydone.filmix.data.repository.FilmixRepository
import io.github.posaydone.filmix.data.repository.SeriesProgressRepository
import io.github.posaydone.filmix.databinding.ActivityPlayerBinding
import io.github.posaydone.filmix.databinding.CustomPlayerViewBinding
import io.github.posaydone.filmix.ui.fragment.EpisodesDialogFragment
import io.github.posaydone.filmix.ui.fragment.PlayerSettingsDialogFragment
import io.github.posaydone.filmix.ui.fragment.TranslationsDialogFragment
import io.github.posaydone.filmix.ui.util.PlaybackPositionListener
import io.github.posaydone.filmix.ui.viewModel.PlayerViewModel
import io.github.posaydone.filmix.ui.viewModel.PlayerViewModelFactory

class PlayerActivity : AppCompatActivity(), PlaybackPositionListener {
    private lateinit var viewModel: PlayerViewModel
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var binding: ActivityPlayerBinding
    private lateinit var controlsBinding: CustomPlayerViewBinding

    private lateinit var episodesDialogFragment: EpisodesDialogFragment
    private lateinit var playerSettingsDialogFragment: PlayerSettingsDialogFragment
    private lateinit var translationsDialogFragment: TranslationsDialogFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        makeFullscreen()

        super.onCreate(savedInstanceState)

        val db = MainDb.getDb(this)
        val showId = intent.getIntExtra("SHOW_ID", 0)

        val seriesProgressRepository = SeriesProgressRepository(db.getDao())
        val filmixRepository = FilmixRepository(FilmixApiClient().getApiService(this))
        val viewModelFactory =
            PlayerViewModelFactory(filmixRepository, seriesProgressRepository, showId)

        episodesDialogFragment = EpisodesDialogFragment()
        playerSettingsDialogFragment = PlayerSettingsDialogFragment()
        translationsDialogFragment = TranslationsDialogFragment()

        viewModel = ViewModelProvider(this, viewModelFactory).get(PlayerViewModel::class.java)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        controlsBinding = CustomPlayerViewBinding.bind(binding.root)

        viewModel.playbackPositionListener = this

        setContentView(binding.root)

        // Инициализируем ExoPlayer
        setupExoplayer()
        observeViewModel()

        controlsBinding.goBack.setOnClickListener {
            this.finish()
        }
    }


    private fun setupExoplayer() {
        exoPlayer = ExoPlayer.Builder(this).build().also { exoPlayer ->
            binding.exoplayerView.player = exoPlayer
        }
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                // STATE_READY, STATE_BUFFERING
                binding.exoplayerView.keepScreenOn =
                    !(playbackState == Player.STATE_IDLE || playbackState == Player.STATE_ENDED)
            }
        })

    }

    private fun makeFullscreen() {
        val activityInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        activityInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    private fun observeViewModel() {
        controlsBinding.playerSettingsButton.setOnClickListener {
            playerSettingsDialogFragment.show(
                supportFragmentManager, "fragment_player_settings_dialog"
            )
        }

        controlsBinding.episodePickerButton.setOnClickListener {
            episodesDialogFragment.show(supportFragmentManager, "fragment_episodes_dialog")
        }

        controlsBinding.translationPickerButton.setOnClickListener {
            translationsDialogFragment.show(supportFragmentManager, "fragment_translation_dialog")
        }

        viewModel.details.observe(this) { details ->
            controlsBinding.showTitleTextView.text = details.title
        }

        viewModel.selectedSeason.observe(this) { season ->
            if (season == null) {
                controlsBinding.seasonTitleTextView.visibility = View.GONE
                return@observe
            }
            controlsBinding.seasonTitleTextView.text = getString(R.string.season, season.season)
        }

        viewModel.selectedEpisode.observe(this) { episode ->
            if (episode == null) {
                controlsBinding.episodeTitleTextView.visibility = View.GONE
                return@observe
            }
            controlsBinding.episodeTitleTextView.text = getString(R.string.episode, episode.episode)
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

        viewModel.contentType.observe(this) { contentType ->
            if (contentType == "movie")
                controlsBinding.episodePickerButton.visibility = View.GONE
        }
    }


    private fun playVideo(url: String) {
        val mediaItem = MediaItem.fromUri(url)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    override fun onPause() {
        super.onPause()
        viewModel.saveProgress(exoPlayer.currentPosition)
        exoPlayer.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.saveProgress(exoPlayer.currentPosition)
        exoPlayer.release()
    }

    override fun onRestorePlaybackPosition(position: Long) {
        exoPlayer.seekTo(position)
    }

}
