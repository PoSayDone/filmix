package io.github.posaydone.filmix.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.posaydone.filmix.data.model.Episode
import io.github.posaydone.filmix.data.model.File
import io.github.posaydone.filmix.data.model.MovieOrSeriesResponse
import io.github.posaydone.filmix.data.model.MovieTranslation
import io.github.posaydone.filmix.data.model.Season
import io.github.posaydone.filmix.data.model.Series
import io.github.posaydone.filmix.data.model.SeriesProgress
import io.github.posaydone.filmix.data.model.Translation
import io.github.posaydone.filmix.data.repository.FilmixRepository
import io.github.posaydone.filmix.data.repository.SeriesProgressRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val filmixRepository: FilmixRepository,
    private val seriesProgressRepository: SeriesProgressRepository,
    private val movieId: Int
) :
    ViewModel() {

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
                    _moviePieces.value = movie
                    _contentType.value = "movie"
                }

                is MovieOrSeriesResponse.SeriesResponse -> {
                    Log.d("contentType", "Series")
                    val seriesTransformed = response.series
                    series = seriesTransformed
                    _contentType.value = "series"
                    _seasons.value = seriesTransformed.seasons
                    restoreSeriesProgress()
                }
            }
        }
    }

    private fun restoreSeriesProgress() {
        viewModelScope.launch {
            val savedSeries = seriesProgressRepository.getSeriesProgressById(movieId).firstOrNull()
            Log.d(TAG, "restoreSeriesProgress: ${savedSeries}")
            if (savedSeries != null) {
                seasons.value?.find { it.season == savedSeries.season }.let {
                    _selectedSeason.value = it
                    selectedSeason.value?.episodes?.find { it.episode == savedSeries.episode }.let {
                        _selectedEpisode.value = it
                        selectedEpisode.value?.translations?.find { it.translation == savedSeries.translation }
                            .let {
                                _selectedTranslation.value = it
                                selectedTranslation.value?.files?.find {
                                    it.quality == savedSeries.quality
                                }.let {
                                    _selectedQuality.value = it
                                }
                            }
                    }
                }
            } else {
                seasons.value?.get(0).let {
                    _selectedSeason.value = it
                    selectedSeason.value?.episodes?.get(0).let {
                        _selectedEpisode.value = it
                    }
                }
            }
        }
    }

    // Function to set the selected season
    fun setSeason(season: Season) {
        _selectedSeason.value = season
        _selectedEpisode.value = null
        _selectedTranslation.value = null
        _selectedQuality.value = null
    }

    // Function to set the selected episode
    fun setEpisode(episode: Episode) {
        _selectedEpisode.value = episode
        // Reset translation and quality when episode changes
        _selectedTranslation.value = null
        _selectedQuality.value = null
    }

    // Function to set the selected translation
    fun setTranslation(translation: Translation) {
        _selectedTranslation.value = translation
        // Reset quality when translation changes
        _selectedQuality.value = null
    }

    fun setMovieTranslation(movieTranslation: MovieTranslation) {
        _selectedMovieTranslation.value = movieTranslation
        _selectedQuality.value = null
    }

    // Function to set the selected quality
    fun setQuality(qualityFile: File) {
        _selectedQuality.value = qualityFile
        updateVideoUrl()
    }

    // Function to update the video URL
    private fun updateVideoUrl() {
        if (contentType.value == "series") {
            val season = selectedSeason.value
            val episode = selectedEpisode.value
            val translation = _selectedTranslation.value
            val qualityFile = _selectedQuality.value

            if (season != null && episode != null && translation != null && qualityFile != null) {
                _videoUrl.value = qualityFile.url
                viewModelScope.launch {
                    seriesProgressRepository.insertSeriesProgress(
                        SeriesProgress(
                            movieId,
                            season.season,
                            episode.episode,
                            translation.translation,
                            qualityFile.quality
                        )
                    )
                }
            }
        } else {
            val translation = _selectedMovieTranslation.value
            val qualityFile = _selectedQuality.value

            if (translation != null && qualityFile != null) {
                val file =
                    _selectedMovieTranslation.value?.files?.find { it.quality == qualityFile.quality }
                _videoUrl.value = file?.url
            }
        }
    }
}
