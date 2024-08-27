package io.github.posaydone.filmix.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.posaydone.filmix.data.model.Episode
import androidx.lifecycle.viewModelScope
import io.github.posaydone.filmix.data.model.MovieOrSeriesResponse
import io.github.posaydone.filmix.data.model.MoviePiece
import io.github.posaydone.filmix.data.model.Season
import io.github.posaydone.filmix.data.model.Series
import io.github.posaydone.filmix.data.model.Translation
import io.github.posaydone.filmix.data.repository.FilmixRepository
import kotlinx.coroutines.launch

class PlayerViewModel(private val repository: FilmixRepository, private val movieId: Int) : ViewModel() {
    private val _translations = MutableLiveData<List<String>>()
    val translations: LiveData<List<String>> get() = _translations

    private val _seasons = MutableLiveData<List<String>>()
    val seasons: LiveData<List<String>> get() = _seasons

    private val _episodes = MutableLiveData<List<Episode>>()
    val episodes: LiveData<List<Episode>> get() = _episodes

    private val _qualities = MutableLiveData<List<Int>>()
    val qualities: LiveData<List<Int>> get() = _qualities

    private val _videoUrl = MutableLiveData<String?>()
    val videoUrl: LiveData<String?> get() = _videoUrl


    // Данные
    lateinit var contentType: String

    private lateinit var series: Series
    private lateinit var movie: List<MoviePiece>

    private var selectedTranslation: Translation? = null
    private var selectedSeason: Season? = null
    private var selectedEpisode: Episode? = null
    private var selectedQuality: Int? = null


    init {
        initialize()
    }

    // Инициализация данных
    fun initialize() {
        viewModelScope.launch {
            val response = repository.fetchSeriesOrMovie(movieId)
            when (response)  {
                is MovieOrSeriesResponse.MovieResponse -> {
                    Log.d("contentType", "Movie")
                    movie = response.movies
                }
                is MovieOrSeriesResponse.SeriesResponse -> {
                    Log.d("contentType", "Series")
                    val translationsData = response.series
                    series = translationsData
                    val translationKeys = translationsData.keys.toList()
                    _translations.value = translationKeys

                    if (translationKeys.isNotEmpty()) {
                        selectTranslation(translationKeys[0])
                    }
                }
            }


        }
    }

    // Выбор озвучки
    fun selectTranslation(translationKey: String) {
        selectedTranslation = series[translationKey]
        selectedTranslation?.let {
            val seasonsList = it.keys.map { it }.sorted()
            _seasons.value = seasonsList

            if (seasonsList.isNotEmpty()) {
                Log.d("Seasons", seasonsList.toString())
                selectSeason(seasonsList[0])
            }
        }
    }

    // Выбор сезона
    fun selectSeason(seasonId: String) {
        selectedSeason = selectedTranslation?.get(seasonId)
        selectedSeason?.let {
            val episodesList = it.episodes.values.toList().sortedBy { it.episode }
            Log.d("Episodes", episodesList.toString())
            _episodes.value = episodesList

            if (episodesList.isNotEmpty()) {
                selectEpisode(0)
            }
        }
    }

    // Выбор серии
    fun selectEpisode(episodeIndex: Int) {
        val episode = episodeIndex +1
        Log.d("Episodes", selectedSeason?.episodes.toString())
        selectedEpisode = selectedSeason?.episodes?.get("e$episode")
        selectedEpisode?.let {
            val qualitiesList = it.files.map { if (it.quality == 480) it.quality else null }
                .filterNotNull()
                .distinct()
                .sortedDescending()
            _qualities.value = qualitiesList

            if (qualitiesList.isNotEmpty()) {
                selectQuality(qualitiesList[0])
            }
        }
    }


    // Выбор качества видео
    fun selectQuality(quality: Int) {
        selectedQuality= quality
        val selectedFile = selectedEpisode?.files?.find { it.quality == quality }
        Log.d("FILE", selectedFile.toString())
        _videoUrl.value = selectedFile?.url
    }

}
