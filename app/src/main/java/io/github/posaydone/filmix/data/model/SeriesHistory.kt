package io.github.posaydone.filmix.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SeriesHistory(
    val season: Int,
    val episode: Int,
    val voiceover: String,
    val time: Long,
    val quality: Int,
) : Parcelable