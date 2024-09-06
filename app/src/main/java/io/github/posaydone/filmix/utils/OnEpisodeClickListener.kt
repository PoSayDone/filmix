package io.github.posaydone.filmix.utils

import io.github.posaydone.filmix.data.network.model.Episode

interface OnEpisodeClickListener {
    fun onEpisodeClick(episode: Episode)
}