package io.github.posaydone.filmix.data.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShowsPage(
    val has_next_page: Boolean,
    val items: List<Show>,
    val page: Int,
    val status: String,
) : Parcelable


@Parcelize
data class LastSeenPage(
    val has_next_page: Boolean,
    val items: List<ShowDetails>,
    val page: Int,
    val status: String
) : Parcelable


@Parcelize
data class Categories(
    val new: ShowsPage,
    val popular: ShowsPage,
    val movies: ShowsPage,
    val series: ShowsPage,
    val cartoons: ShowsPage,
    val animatedSeries: ShowsPage,
    val documentary: ShowsPage,
) : Parcelable


