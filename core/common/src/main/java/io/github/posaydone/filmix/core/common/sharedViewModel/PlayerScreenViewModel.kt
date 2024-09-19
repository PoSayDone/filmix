package io.github.posaydone.filmix.core.common.sharedViewModel

import android.content.pm.ActivityInfo
import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.posaydone.filmix.core.data.FilmixRepository
import io.github.posaydone.filmix.core.model.Episode
import io.github.posaydone.filmix.core.model.File
import io.github.posaydone.filmix.core.model.Season
import io.github.posaydone.filmix.core.model.Series
import io.github.posaydone.filmix.core.model.ShowDetails
import io.github.posaydone.filmix.core.model.ShowProgressItem
import io.github.posaydone.filmix.core.model.ShowResourceResponse
import io.github.posaydone.filmix.core.model.Translation
import io.github.posaydone.filmix.core.model.VideoWithQualities
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@androidx.media3.common.util.UnstableApi
data class PlayerState(
    val isPlaying: Boolean = false,
    val totalTime: Long = 0L,
    val resizeMode: Int = AspectRatioFrameLayout.RESIZE_MODE_FIT,
    val orientation: Int = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE,
)

@Immutable
sealed class VideoPlayerScreenUiState {
    object Loading : VideoPlayerScreenUiState()
    object Error : VideoPlayerScreenUiState()
    data class Done(val showDetails: ShowDetails) : VideoPlayerScreenUiState()
}

@androidx.media3.common.util.UnstableApi
@HiltViewModel
class PlayerScreenViewModel @Inject constructor(
    val player: ExoPlayer,
    private val repository: FilmixRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val TAG: String = "PlayerVIewModel"

    private val showId = checkNotNull(savedStateHandle.get<Int>("showId"))

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState = _playerState.asStateFlow()

    private val _selectedSeason = MutableStateFlow<Season?>(null)
    val selectedSeason: StateFlow<Season?> = _selectedSeason.asStateFlow()

    private val _selectedEpisode = MutableStateFlow<Episode?>(null)
    val selectedEpisode: StateFlow<Episode?> = _selectedEpisode.asStateFlow()

    private val _selectedTranslation = MutableStateFlow<Translation?>(null)
    val selectedTranslation: StateFlow<Translation?> = _selectedTranslation.asStateFlow()

    private val _selectedQuality = MutableStateFlow<File?>(null)
    val selectedQuality: StateFlow<File?> = _selectedQuality.asStateFlow()

    private val _seasons = MutableStateFlow<List<Season>?>(null)
    val seasons: StateFlow<List<Season>?> = _seasons.asStateFlow()


    private val _moviePieces = MutableStateFlow<List<VideoWithQualities>?>(null)
    val moviePieces: StateFlow<List<VideoWithQualities>?> = _moviePieces.asStateFlow()

    private val _selectedMovieTranslation = MutableStateFlow<VideoWithQualities?>(null)
    val selectedMovieTranslation: StateFlow<VideoWithQualities?> =
        _selectedMovieTranslation.asStateFlow()


    private val _details = MutableStateFlow<ShowDetails?>(null)
    val details = _details.asStateFlow()

    private val _contentType = MutableStateFlow<ShowType?>(null)
    val contentType: StateFlow<ShowType?> = _contentType.asStateFlow()

    // StateFlow for the final video URL
    private val _videoUrl = MutableStateFlow<String?>(null)
    val videoUrl = _videoUrl.asStateFlow()


    private lateinit var series: Series
    private lateinit var movie: List<VideoWithQualities>

    init {
        player.prepare()
        initialize()
    }

    // Инициализация данных
    private fun initialize() {
        viewModelScope.launch {
            when (val response = repository.getShowResource(showId)) {
                is ShowResourceResponse.MovieResourceResponse -> {
                    movie = response.movies
                    _details.value = repository.getShowDetails(showId)
                    _moviePieces.value = movie
                    _contentType.value = ShowType.MOVIE
                    restoreMovieProgress()
                }

                is ShowResourceResponse.SeriesResourceResponse -> {
                    val seriesTransformed = response.series
                    series = seriesTransformed
                    _details.value = repository.getShowDetails(showId)
                    _contentType.value = ShowType.SERIES
                    _seasons.value = seriesTransformed.seasons
                    restoreSeriesProgress()
                }
            }
        }
    }

    private fun restoreMovieProgress() {
        viewModelScope.launch {
            val savedMovieHistory = repository.getShowProgress(showId)

            if (savedMovieHistory.isNotEmpty()) {
                restoreMovieSavedProgress(savedMovieHistory.first())
            } else {
                setDefaultMovieProgress()
            }
        }
        movie.get(0).let {
            _selectedMovieTranslation.value = it
            selectedMovieTranslation.value?.files?.get(0).let {
                _selectedQuality.value = it
            }
        }
    }

    private fun restoreMovieSavedProgress(savedMovie: ShowProgressItem) {
        val translation = moviePieces.value?.find { it.voiceover == savedMovie.voiceover }
        _selectedMovieTranslation.value = translation

        val file = translation?.files?.find {
            it.quality == savedMovie.quality || it.quality == 1080
        } ?: translation?.files?.getOrNull(0)
        _selectedQuality.value = file
        file?.url?.let {
            _videoUrl.value = it
        }
        playVideo(savedMovie.time)
    }

    private fun setDefaultMovieProgress() {
        val defaultTranslation = moviePieces.value?.getOrNull(0)
        _selectedMovieTranslation.value = defaultTranslation

        val defaultFile = defaultTranslation?.files?.getOrNull(0)
        _selectedQuality.value = defaultFile

        defaultFile?.url?.let {
            _videoUrl.value = it
        }
        playVideo()
    }

    private fun restoreSeriesProgress() {
        viewModelScope.launch {
            val savedSeriesHistory = repository.getShowProgress(showId)

            if (savedSeriesHistory.isNotEmpty()) {
                restoreSeriesSavedProgress(savedSeriesHistory.first())
            } else {
                setDefaultSeriesProgress()
            }
        }
    }

    private fun restoreSeriesSavedProgress(savedSeries: ShowProgressItem) {
        val season = seasons.value?.find { it.season == savedSeries.season }
        _selectedSeason.value = season

        val episode = season?.episodes?.find { it.episode == savedSeries.episode }
        _selectedEpisode.value = episode

        val translation = episode?.translations?.find {
            it.translation.equals(savedSeries.voiceover, ignoreCase = true)
        }
        _selectedTranslation.value = translation

        val file = translation?.files?.find {
            it.quality == savedSeries.quality || it.quality == 1080
        } ?: translation?.files?.getOrNull(0)
        _selectedQuality.value = file

        file?.url?.let {
            _videoUrl.value = it
        }
        playVideo(savedSeries.time)
    }

    private fun setDefaultSeriesProgress() {
        val defaultSeason = seasons.value?.getOrNull(0)
        _selectedSeason.value = defaultSeason

        val defaultEpisode = defaultSeason?.episodes?.getOrNull(0)
        _selectedEpisode.value = defaultEpisode

        val defaultTranslation = defaultEpisode?.translations?.getOrNull(0)
        _selectedTranslation.value = defaultTranslation

        val defaultFile = defaultTranslation?.files?.getOrNull(0)
        _selectedQuality.value = defaultFile

        defaultFile?.url?.let {
            _videoUrl.value = it
        }

        playVideo()
    }

    // Function to set the selected season
    fun setSeason(season: Season) {
        _selectedSeason.value = season
    }

    // Function to set the selected episode
    fun setEpisode(episode: Episode) {
        val oldTranslation = selectedTranslation.value?.translation
        val oldQuality = selectedQuality.value?.quality

        _selectedEpisode.value = episode

        val oldTranslationInNewEpisode =
            selectedEpisode.value?.translations?.find { it.translation == oldTranslation }
        if (oldTranslationInNewEpisode != null) {
            _selectedTranslation.value = oldTranslationInNewEpisode
            val oldQualityInNewEpisode =
                selectedTranslation.value?.files?.find { it.quality == oldQuality }
            if (oldQualityInNewEpisode != null) {
                _selectedQuality.value = oldQualityInNewEpisode
            } else {
                _selectedQuality.value = selectedTranslation.value?.files?.get(0)
            }

        } else {
            _selectedTranslation.value = selectedEpisode.value?.translations?.get(0)
            _selectedQuality.value = selectedTranslation.value?.files?.get(0)
        }
        _videoUrl.value = selectedQuality.value?.url
        saveProgress()
        playVideo()
    }

    // Function to set the selected translation
    fun setTranslation(translation: Translation) {
        val oldQuality = selectedQuality.value?.quality
        _selectedTranslation.value = translation

        val oldQualityInNewEpisode =
            selectedTranslation.value?.files?.find { it.quality == oldQuality }
        if (oldQualityInNewEpisode != null) {
            _selectedQuality.value = oldQualityInNewEpisode
        } else {
            _selectedQuality.value = selectedTranslation.value?.files?.get(0)
        }
        _videoUrl.value = selectedQuality.value?.url
        saveProgress()
        playVideo()
    }

    fun setMovieTranslation(movieTranslation: VideoWithQualities) {
        val oldQuality = selectedQuality.value?.quality
        _selectedMovieTranslation.value = movieTranslation

        val oldQualityInNewEpisode =
            selectedTranslation.value?.files?.find { it.quality == oldQuality }
        if (oldQualityInNewEpisode != null) {
            _selectedQuality.value = oldQualityInNewEpisode
        } else {
            _selectedQuality.value = selectedMovieTranslation.value?.files?.get(0)
        }
        _videoUrl.value = selectedQuality.value?.url
        saveProgress()
        playVideo()
    }

    // Function to set the selected quality
    fun setQuality(qualityFile: File) {
        _selectedQuality.value = qualityFile
        _videoUrl.value = selectedQuality.value?.url
        saveProgress()
        playVideo()
    }


    fun playVideo(time: Long = 0L) {
        videoUrl.value?.let { url ->
            val mediaItem = MediaItem.fromUri(url)
            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
            if (time != 0L) {
                player.seekTo(time)
            }
            _playerState.update {
                it.copy(
                    isPlaying = true, totalTime = mediaItem.mediaMetadata.durationMs ?: 0L
                )
            }
        }
    }

    fun saveProgress() {
        val season = selectedSeason.value
        val episode = selectedEpisode.value
        val translation = _selectedTranslation.value
        val movieTranslation = _selectedMovieTranslation.value
        val qualityFile = _selectedQuality.value
        val time = player.currentPosition

        if (contentType.value == ShowType.MOVIE) {
            if (movieTranslation != null && qualityFile != null) viewModelScope.launch {
                val savedSeriesProgress = ShowProgressItem(
                    0,
                    0,
                    movieTranslation.voiceover,
                    time,
                    qualityFile.quality,
                )
                repository.addShowProgress(showId, savedSeriesProgress)
            }
        } else {
            if (season != null && episode != null && translation != null && qualityFile != null) viewModelScope.launch {
                val savedSeriesProgress = ShowProgressItem(
                    season.season,
                    episode.episode,
                    translation.translation,
                    time,
                    qualityFile.quality,
                )
                repository.addShowProgress(showId, savedSeriesProgress)
            }
        }
    }

    fun onPlayPauseClick() {
        if (player.isPlaying) {
            player.pause().also {
                _playerState.update {
                    it.copy(isPlaying = false)
                }
            }
        } else {
            player.play().also {
                _playerState.update {
                    it.copy(isPlaying = true)
                }
            }
        }
    }

    fun setResizeMode(resizeMode: Int) {
        _playerState.update {
            it.copy(resizeMode = resizeMode)
        }
    }
}

enum class ShowType {
    MOVIE, SERIES
}
