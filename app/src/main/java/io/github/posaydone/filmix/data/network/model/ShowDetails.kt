package io.github.posaydone.filmix.data.network.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShowDetails(
    @SerializedName("id") var id: Int,
    @SerializedName("category") var category: String? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("original_title") var originalTitle: String? = null,
    @SerializedName("year") var year: Int? = null,
    @SerializedName("updated") var updated: String? = null,
    @SerializedName("actors") var actors: ArrayList<Person>? = null,
    @SerializedName("directors") var directors: ArrayList<Person>? = null,
    @SerializedName("last_episode") var lastEpisode: LastEpisode? = LastEpisode(),
    @SerializedName("max_episode") var maxEpisode: MaxEpisode? = MaxEpisode(),
    @SerializedName("countries") var countries: ArrayList<Country> = arrayListOf(),
    @SerializedName("genres") var genres: ArrayList<Genre> = arrayListOf(),
    @SerializedName("poster") var poster: String? = null,
    @SerializedName("rip") var rip: String? = null,
    @SerializedName("quality") var quality: String? = null,
    @SerializedName("votesPos") var votesPos: Int,
    @SerializedName("votesNeg") var votesNeg: Int,
    @SerializedName("ratingImdb") var ratingImdb: Double? = null,
    @SerializedName("ratingKinopoisk") var ratingKinopoisk: Double? = null,
    @SerializedName("url") var url: String? = null,
    @SerializedName("duration") var duration: Int? = null,
    @SerializedName("votesIMDB") var votesIMDB: Int? = null,
    @SerializedName("votesKinopoisk") var votesKinopoisk: Int? = null,
    @SerializedName("idKinopoisk") var idKinopoisk: Int? = null,
    @SerializedName("mpaa") var mpaa: String? = null,
    @SerializedName("slogan") var slogan: String? = null,
    @SerializedName("short_story") var shortStory: String? = null,
    @SerializedName("status") var status: ShowStatus? = ShowStatus(),
    @SerializedName("is_favorite") var isFavorite: Boolean? = null,
    @SerializedName("is_deferred") var isDeferred: Boolean? = null,
    @SerializedName("is_hdr") var isHdr: Boolean? = null
) : Parcelable

@Parcelize
data class Genre(
    val alt_name: String,
    val id: Int,
    val name: String
) : Parcelable

@Parcelize
data class Country(
    val id: Int,
    val name: String
) : Parcelable

@Parcelize
data class Person(
    @SerializedName("id") var id: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("poster") var poster: String? = null
) : Parcelable

@Parcelize
data class MaxEpisode(
    val season: Int? = null,
    val episode: Int? = null,
) : Parcelable

@Parcelize
data class LastEpisode(
    var season: Int? = null,
    var episode: String? = null,
    var translation: String? = null,
    var date: String? = null
) : Parcelable
