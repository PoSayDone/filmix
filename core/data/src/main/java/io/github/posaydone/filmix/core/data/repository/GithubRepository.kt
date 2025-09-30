package io.github.posaydone.filmix.core.data.repository

import io.github.posaydone.filmix.core.model.GithubRelease
import io.github.posaydone.filmix.core.network.dataSource.GithubDataSource
import javax.inject.Inject

class GithubRepository @Inject constructor(
    private val githubDataSource: GithubDataSource,
) {
    suspend fun getLatestRelease(): Result<GithubRelease> {
        return try {
            githubDataSource.getLatestRelease()
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}