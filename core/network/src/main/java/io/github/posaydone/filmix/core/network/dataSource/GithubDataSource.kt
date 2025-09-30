package io.github.posaydone.filmix.core.network.dataSource

import io.github.posaydone.filmix.core.model.GithubRelease
import io.github.posaydone.filmix.core.network.service.GithubApiService
import javax.inject.Inject

class GithubDataSource @Inject constructor(
    private val githubApiService: GithubApiService,
) {
    suspend fun getLatestRelease(): Result<GithubRelease> {
        return try {
            val response = githubApiService.getLatestRelease()
            if (response.isSuccessful) {
                response.body()?.let { release ->
                    Result.success(release)
                } ?: Result.failure(Exception("Response body is null"))
            } else {
                Result.failure(Exception("API request failed with code: ${response.code()}, message: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}