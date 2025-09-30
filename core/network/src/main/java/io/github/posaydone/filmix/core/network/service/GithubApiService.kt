package io.github.posaydone.filmix.core.network.service

import io.github.posaydone.filmix.core.model.GithubRelease
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface GithubApiService {
    @GET("repos/posaydone/filmix/releases/latest")
    suspend fun getLatestRelease(): Response<GithubRelease>
    
    @GET
    suspend fun downloadFile(@Url fileUrl: String): Response<okio.ResponseBody>
}