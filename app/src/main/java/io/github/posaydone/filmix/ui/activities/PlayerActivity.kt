package io.github.posaydone.filmix.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import io.github.posaydone.filmix.data.api.RetrofitClient
import io.github.posaydone.filmix.data.repository.FilmixRepository
import io.github.posaydone.filmix.databinding.ActivityPlayerBinding
import io.github.posaydone.filmix.databinding.CustomPlayerViewBinding
import io.github.posaydone.filmix.ui.viewmodels.PlayerViewModel
import io.github.posaydone.filmix.ui.viewmodels.PlayerViewModelFactory

class PlayerActivity : AppCompatActivity() {

    private lateinit var viewModel: PlayerViewModel
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var binding: ActivityPlayerBinding
    private lateinit var controlsBinding: CustomPlayerViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val movieId = intent.getIntExtra("MOVIE_ID", 11)

        val repository = FilmixRepository(RetrofitClient.apiService)
        val viewModelFactory = PlayerViewModelFactory(repository, movieId)
        viewModel = ViewModelProvider(this, viewModelFactory).get(PlayerViewModel::class.java)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        controlsBinding = CustomPlayerViewBinding.bind(binding.root)
        setContentView(binding.root)
        hideSystemUI()


        // Инициализируем ExoPlayer
        initializePlayer(movieId)
    }

    private fun initializePlayer(movieId: Int) {
        exoPlayer = ExoPlayer.Builder(this)
            .build()
            .also { exoPlayer ->
                binding.exoplayerView.player = exoPlayer
            }

        // Наблюдение за изменениями в сериях
        viewModel.episodes.observe(this) { episodes ->
            val episodesList = episodes.map { it.episode }
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                episodesList
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            controlsBinding.episodesSpinner.adapter = adapter
        }

        // Наблюдение за изменениями в сезонах
        viewModel.seasons.observe(this) { seasons ->
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                seasons
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            controlsBinding.seasonsSpinner.adapter = adapter
        }

        // Наблюдение за изменениями в переводах
        viewModel.translations.observe(this) { translations ->
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                translations
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            controlsBinding.translationSpinner.adapter = adapter
        }


        // Наблюдение за изменениями в качестве
        viewModel.qualities.observe(this) { qualities ->
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                qualities
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            controlsBinding.qualitySpinner.adapter = adapter
        }

        // Наблюдение за URL видео
        viewModel.videoUrl.observe(this) { url ->
            if (url?.isNotEmpty() == true) {
                playVideo(url)
            }
        }

        // Обработчики выбора
        controlsBinding.translationSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (view != null) {
                        val selectedTranslation = parent.getItemAtPosition(position) as String
                        viewModel.selectTranslation(selectedTranslation)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

        controlsBinding.seasonsSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (view != null) {
                        val selectedSeason = parent.getItemAtPosition(position) as String
                        viewModel.selectSeason(selectedSeason)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

        controlsBinding.episodesSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (view != null) {
                        viewModel.selectEpisode(position)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }


        controlsBinding.qualitySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (view != null) {
                        val selectedQuality = parent.getItemAtPosition(position) as Int
                        Log.d("Quality", selectedQuality.toString())
                        viewModel.selectQuality(selectedQuality)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
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
