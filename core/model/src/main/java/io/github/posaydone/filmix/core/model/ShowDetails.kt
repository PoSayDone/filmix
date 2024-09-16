package io.github.posaydone.filmix.core.model

import com.google.gson.annotations.SerializedName

data class ShowDetails(
    @SerializedName("id") var id: Int,
    @SerializedName("category") var category: String? = null,
    @SerializedName("title") var title: String,
    @SerializedName("original_title") var originalTitle: String,
    @SerializedName("year") var year: Int,
    @SerializedName("updated") var updated: String? = null,
    @SerializedName("actors") var actors: ArrayList<Person>? = null,
    @SerializedName("directors") var directors: ArrayList<Person?>? = arrayListOf(),
    @SerializedName("last_episode") var lastEpisode: LastEpisode? = LastEpisode(),
    @SerializedName("max_episode") var maxEpisode: MaxEpisode? = MaxEpisode(),
    @SerializedName("countries") var countries: ArrayList<Country> = arrayListOf(),
    @SerializedName("genres") var genres: ArrayList<Genre> = arrayListOf(),
    @SerializedName("poster") var poster: String? = null,
    @SerializedName("rip") var rip: String? = null,
    @SerializedName("quality") var quality: String? = null,
    @SerializedName("votesPos") var votesPos: Int,
    @SerializedName("votesNeg") var votesNeg: Int,
    @SerializedName("ratingImdb") var ratingImdb: Double,
    @SerializedName("ratingKinopoisk") var ratingKinopoisk: Double,
    @SerializedName("url") var url: String? = null,
    @SerializedName("duration") var duration: Int? = null,
    @SerializedName("votesIMDB") var votesIMDB: Int? = null,
    @SerializedName("votesKinopoisk") var votesKinopoisk: Int? = null,
    @SerializedName("idKinopoisk") var idKinopoisk: Int? = null,
    @SerializedName("mpaa") var mpaa: String? = null,
    @SerializedName("slogan") var slogan: String? = null,
    @SerializedName("short_story") var shortStory: String,
    @SerializedName("status") var status: ShowStatus? = ShowStatus(),
    @SerializedName("is_favorite") var isFavorite: Boolean? = null,
    @SerializedName("is_deferred") var isDeferred: Boolean? = null,
    @SerializedName("is_hdr") var isHdr: Boolean? = null,
)

data class Genre(
    val alt_name: String,
    val id: Int,
    val name: String,
)

data class Country(
    val id: Int,
    val name: String,
)

data class Person(
    var id: String,
    var name: String,
    var poster: String,
)

data class MaxEpisode(
    val season: Int? = null,
    val episode: Int? = null,
)

data class LastEpisode(
    var season: Int? = null,
    var episode: String? = null,
    var translation: String? = null,
    var date: String? = null,
)
