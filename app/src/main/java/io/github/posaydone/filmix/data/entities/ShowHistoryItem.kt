package io.github.posaydone.filmix.data.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShowHistoryItem(
    val season: Int,
    val episode: Int,
    val voiceover: String,
    val time: Long,
    val quality: Int,
) : Parcelable