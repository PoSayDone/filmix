package io.github.posaydone.filmix.core.network.utils

import io.github.posaydone.filmix.core.model.Episode
import io.github.posaydone.filmix.core.model.File
import io.github.posaydone.filmix.core.model.FilmixSeries
import io.github.posaydone.filmix.core.model.Season
import io.github.posaydone.filmix.core.model.Series
import io.github.posaydone.filmix.core.model.Translation

fun transformSeries(filmixSeries: FilmixSeries): Series {
    val transformedSeasons = mutableListOf<Season>()

    filmixSeries.forEach { (translationKey, translationValue) ->
        translationValue.forEach { (_, seasonValue) ->
            var season = transformedSeasons.find { it.season == seasonValue.season }
            if (season == null) {
                season = Season(
                    seasonValue.season,
                    mutableListOf()
                )
                transformedSeasons.add(season)
            }
            seasonValue.episodes.forEach { (_, episodeValue) ->
                var episode = season.episodes.find { it.episode == episodeValue.episode }
                if (episode == null) {
                    episode = Episode(
                        episodeValue.episode,
                        episodeValue.ad_skip,
                        episodeValue.title,
                        episodeValue.released,
                        mutableListOf()
                    )
                    season.episodes.add(episode)
                }
                val tranlationWithFiles = Translation(
                    translationKey,
                    episodeValue.files.map { file ->
                        File(
                            url = file.url,
                            quality = file.quality,
                            proPlus = file.proPlus
                        )
                    }
                )
                episode.translations.add(tranlationWithFiles)
            }
        }
    }

    return Series(seasons = transformedSeasons)
}
