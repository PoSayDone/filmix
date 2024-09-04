package io.github.posaydone.filmix.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.posaydone.filmix.data.model.Episode
import io.github.posaydone.filmix.data.model.File
import io.github.posaydone.filmix.data.model.MovieDetails
import io.github.posaydone.filmix.data.model.MovieOrSeriesResponse
import io.github.posaydone.filmix.data.model.MovieTranslation
import io.github.posaydone.filmix.data.model.Season
import io.github.posaydone.filmix.data.model.Series
import io.github.posaydone.filmix.data.model.SeriesHistory
import io.github.posaydone.filmix.data.model.Translation
import io.github.posaydone.filmix.data.repository.FilmixRepository
import io.github.posaydone.filmix.data.repository.SeriesProgressRepository
import io.github.posaydone.filmix.ui.utils.PlaybackPositionListener
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


    private val _moviePieces = MutableLiveData<List<MovieTranslation>?>()
    val moviePieces: LiveData<List<MovieTranslation>?> get() = _moviePieces

    private val _selectedMovieTranslation = MutableLiveData<MovieTranslation?>()
    val selectedMovieTranslation: LiveData<MovieTranslation?> get() = _selectedMovieTranslation

    private val _details = MutableLiveData<MovieDetails>()
    val details: LiveData<MovieDetails> get() = _details

    private val _contentType = MutableLiveData<String>()
    val contentType: LiveData<String> get() = _contentType

    // LiveData for the final video URL
    private val _videoUrl = MutableLiveData<String>()
    val videoUrl: LiveData<String> get() = _videoUrl


    private lateinit var series: Series
    private lateinit var movie: List<MovieTranslation>


    init {
        initialize()
    }

    // Инициализация данных
    fun initialize() {
        viewModelScope.launch {
            when (val response = filmixRepository.fetchSeriesOrMovie(movieId)) {
                is MovieOrSeriesResponse.MovieResponse -> {
                    Log.d("contentType", "Movie")
                    movie = response.movies
                    _details.value = filmixRepository.fetchMovieDetails(movieId)
                    _moviePieces.value = movie
                    _contentType.value = "movie"
                    restoreMovieProgress()
                }

                is MovieOrSeriesResponse.SeriesResponse -> {
                    Log.d("contentType", "Series")
                    val seriesTransformed = response.series
                    series = seriesTransformed
                    _details.value = filmixRepository.fetchMovieDetails(movieId)
                    _contentType.value = "series"
                    _seasons.value = seriesTransformed.seasons
                    restoreSeriesProgress()
                }
            }
        }
    }

    private fun restoreMovieProgress() {
        movie.get(0).let {
            _selectedMovieTranslation.value = it
            selectedMovieTranslation.value?.files?.get(0).let {
                _selectedQuality.value = it
            }
        }
    }

    private fun restoreSeriesProgress() {
        viewModelScope.launch {
            val savedSeriesHistory = filmixRepository.getSeriesHistory(movieId)

            if (savedSeriesHistory.isNotEmpty()) {
                restoreSeriesSavedProgress(savedSeriesHistory.first()!!)
            } else {
                setDefaultSeriesProgress()
            }
        }
    }

    private fun restoreSeriesSavedProgress(savedSeries: SeriesHistory) {
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

    fun setMovieTranslation(movieTranslation: MovieTranslation) {
        val oldQuality = selectedQuality.value?.quality
        _selectedMovieTranslation.value = movieTranslation

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
        val qualityFile = _selectedQuality.value
        val time = if (playbackPosition != null) playbackPosition else 0L

        if (season != null && episode != null && translation != null && qualityFile != null) {
            viewModelScope.launch {
                val savedSeriesProgress = SeriesHistory(
                    season.season,
                    episode.episode,
                    translation.translation,
                    time,
                    qualityFile.quality,
                )
                filmixRepository.setSeriesHistory(movieId, savedSeriesProgress)
            }
        }
    }
}
