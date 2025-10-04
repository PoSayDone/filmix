package io.github.posaydone.filmix.core.model.fanart

import com.google.gson.annotations.SerializedName

data class FanartTvResponse(
    @SerializedName("name") val name: String?,
    @SerializedName("tmdb_id") val tmdbId: String?,
    @SerializedName("imdb_id") val imdbId: String?,
    @SerializedName("tvposter") val tvposter: List<FanartImage>?,
    @SerializedName("tvbanner") val tvbanner: List<FanartImage>?,
    @SerializedName("tvthumb") val tvthumb: List<FanartImage>?,
    @SerializedName("showbackground") val showbackground: List<FanartImage>?,
    @SerializedName("seasonposter") val seasonposter: List<FanartImage>?,
    @SerializedName("seasonbanner") val seasonbanner: List<FanartImage>?,
    @SerializedName("seasonthumb") val seasonthumb: List<FanartImage>?,
    @SerializedName("hdtvlogo") val hdtvlogo: List<FanartImage>?,
    @SerializedName("hdclearart") val hdclearart: List<FanartImage>?,
    @SerializedName("clearlogo") val clearlogo: List<FanartImage>?,
    @SerializedName("clearart") val clearart: List<FanartImage>?,
    @SerializedName("characterart") val characterart: List<FanartImage>?
)

data class FanartMovieResponse(
    @SerializedName("name") val name: String?,
    @SerializedName("tmdb_id") val tmdbId: String?,
    @SerializedName("imdb_id") val imdbId: String?,
    @SerializedName("movieposter") val movieposter: List<FanartImage>?,
    @SerializedName("moviebackground") val moviebackground: List<FanartImage>?,
    @SerializedName("moviethumb") val moviethumb: List<FanartImage>?,
    @SerializedName("moviebanner") val moviebanner: List<FanartImage>?,
    @SerializedName("hdmovieclearart") val hdmovieclearart: List<FanartImage>?,
    @SerializedName("hdmovielogo") val hdmovielogo: List<FanartImage>?,
    @SerializedName("moviedisc") val moviedisc: List<FanartImage>?,
    @SerializedName("movielogo") val movielogo: List<FanartImage>?,
    @SerializedName("movieart") val movieart: List<FanartImage>?,
    @SerializedName("moviecollections") val moviecollections: List<FanartImage>?,
    @SerializedName("movieright") val movieright: List<FanartImage>?
)

data class FanartImage(
    @SerializedName("id") val id: String?,
    @SerializedName("url") val url: String?,
    @SerializedName("lang") val lang: String?,
    @SerializedName("likes") val likes: String?
)