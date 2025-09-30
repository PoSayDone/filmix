package io.github.posaydone.filmix.core.model

import com.google.gson.annotations.SerializedName

data class GithubRelease(
    val id: Int,
    @SerializedName("tag_name")
    val tagName: String,
    @SerializedName("name")
    val releaseName: String,
    @SerializedName("published_at")
    val publishedAt: String,
    val body: String,
    @SerializedName("assets")
    val releaseAssets: List<ReleaseAsset>,
    @SerializedName("html_url")
    val htmlUrl: String
)

data class ReleaseAsset(
    val id: Int,
    val name: String,
    @SerializedName("content_type")
    val contentType: String,
    @SerializedName("browser_download_url")
    val downloadUrl: String,
    val size: Long
)