package io.github.posaydone.filmix.presentation.ui.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.posaydone.filmix.data.network.model.Episode
import io.github.posaydone.filmix.data.network.model.File
import io.github.posaydone.filmix.data.network.model.Season
import io.github.posaydone.filmix.data.network.model.Series
import io.github.posaydone.filmix.data.network.model.ShowDetails
import io.github.posaydone.filmix.data.network.model.ShowHistoryItem
import io.github.posaydone.filmix.data.network.model.ShowResponse
import io.github.posaydone.filmix.data.network.model.Translation
import io.github.posaydone.filmix.data.network.model.VideoWithQualities
import io.github.posaydone.filmix.data.repository.FilmixRepository
import io.github.posaydone.filmix.data.repository.SeriesProgressRepository
import io.github.posaydone.filmix.ui.util.PlaybackPositionListener
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val filmixRepository: FilmixRepository,
    private val seriesProgressRepository: SeriesProgressRepository,
    private val movieId: Int
) :
    ViewModel() {

    var playbackPositionListener: PlaybackPositionListener? = null

    private val TAG: String = "PlayerVIewModel"

    private val _selectedSeason = MutableLiveData<Season?>()
    val selectedSeason: LiveData<Season?> get() = _selectedSeason

    private val _selectedEpisode = MutableLiveData<Episode?>()
    val selectedEpisode: LiveData<Episode?> get() = _selectedEpisode

    private val _selectedTranslation = MutableLiveData<Translation?>()
    val selectedTranslation: LiveData<Translation?> get() = _selectedTranslation

    private val _selectedQuality = MutableLiveData<File?>()
    val selectedQuality: LiveData<File?> get() = _selectedQuality

    private val _seasons = MutableLiveData<List<Season>?>()
    val seasons: LiveData<List<Season>?> get() = _seasons


    private val _moviePieces = MutableLiveData<List<VideoWithQualities>?>()
    val moviePieces: LiveData<List<VideoWithQualities>?> get() = _moviePieces

    private val _selectedMovieTranslation = MutableLiveData<VideoWithQualities?>()
    val selectedMovieTranslation: LiveData<VideoWithQualities?> get() = _selectedMovieTranslation


    private val _details = MutableLiveData<ShowDetails>()
    val details: LiveData<ShowDetails> get() = _details

    private val _contentType = MutableLiveData<String>()
    val contentType: LiveData<String> get() = _contentType

    // LiveData for the final video URL
    private val _videoUrl = MutableLiveData<String>()
    val videoUrl: LiveData<String> get() = _videoUrl


    private lateinit var series: Series
    private lateinit var movie: List<VideoWithQualities>

    init {
        initialize()
    }

    // Инициализация данных
    fun initialize() {
        viewModelScope.launch {
            when (val response = filmixRepository.getShow(movieId)) {
                is ShowResponse.MovieResponse -> {
                    Log.d("contentType", "Movie")
                    movie = response.movies
                    _details.value = filmixRepository.getShowDetails(movieId)
                    _moviePieces.value = movie
                    _contentType.value = "movie"
                    restoreMovieProgress()
                }

                is ShowResponse.SeriesResponse -> {
                    Log.d("contentType", "Series")
                    val seriesTransformed = response.series
                    series = seriesTransformed
                    _details.value = filmixRepository.getShowDetails(movieId)
                    _contentType.value = "series"
                    _seasons.value = seriesTransformed.seasons
                    restoreSeriesProgress()
                }
            }
        }
    }

    private fun restoreMovieProgress() {
        viewModelScope.launch {
            val savedMovieHistory = filmixRepository.getShowHistory(movieId)

            if (savedMovieHistory.isNotEmpty()) {
                restoreMovieSavedProgress(savedMovieHistory.first()!!)
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

    private fun restoreMovieSavedProgress(savedMovie: ShowHistoryItem) {
        val translation = moviePieces.value?.find { it.voiceover == savedMovie.voiceover }
        _selectedMovieTranslation.value = translation

        val file = translation?.files?.find {
            it.quality == savedMovie.quality || it.quality == 1080
        }
        _selectedQuality.value = file

        file?.url?.let {
            _videoUrl.value = it
            playbackPositionListener?.onRestorePlaybackPosition(savedMovie.time)
        }

    }

    private fun setDefaultMovieProgress() {
        val defaultTranslation = moviePieces.value?.getOrNull(0)
        _selectedMovieTranslation.value = defaultTranslation

        val defaultFile = defaultTranslation?.files?.getOrNull(0)
        _selectedQuality.value = defaultFile

        defaultFile?.url?.let {
            _videoUrl.value = it
        }
    }

    private fun restoreSeriesProgress() {
        viewModelScope.launch {
            val savedSeriesHistory = filmixRepository.getShowHistory(movieId)

            if (savedSeriesHistory.isNotEmpty()) {
                restoreSeriesSavedProgress(savedSeriesHistory.first()!!)
            } else {
                setDefaultSeriesProgress()
            }
        }
    }

    private fun restoreSeriesSavedProgress(savedSeries: ShowHistoryItem) {
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
        }
        _selectedQuality.value = file

        file?.url?.let {
            _videoUrl.value = it
            playbackPositionListener?.onRestorePlaybackPosition(savedSeries.time)
        }
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
    }

    // Function to set the selected quality
    fun setQuality(qualityFile: File) {
        _selectedQuality.value = qualityFile
        _videoUrl.value = selectedQuality.value?.url
        saveProgress()
    }

    fun saveProgress(playbackPosition: Long? = null) {
        val season = selectedSeason.value
        val episode = selectedEpisode.value
        val translation = _selectedTranslation.value
        val movietranslation = _selectedMovieTranslation.value
        val qualityFile = _selectedQuality.value
        val time = if (playbackPosition != null) playbackPosition else 0L

        if (contentType.value == "movie") {
            if (movietranslation != null && qualityFile != null)
                viewModelScope.launch {
                    val savedSeriesProgress = ShowHistoryItem(
                        0,
                        0,
                        movietranslation.voiceover,
                        time,
                        qualityFile.quality,
                    )
                    filmixRepository.setShowHistory(movieId, savedSeriesProgress)
                }
        } else {
            if (season != null && episode != null && translation != null && qualityFile != null)
                viewModelScope.launch {
                    val savedSeriesProgress = ShowHistoryItem(
                        season.season,
                        episode.episode,
                        translation.translation,
                        time,
                        qualityFile.quality,
                    )
                    filmixRepository.setShowHistory(movieId, savedSeriesProgress)
                }
        }
    }
}
