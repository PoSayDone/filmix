package io.github.posaydone.filmix.core.data.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.posaydone.filmix.core.data.repository.GithubRepository
import io.github.posaydone.filmix.core.network.dataSource.GithubDataSource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GithubDataModule {
    
    @Provides
    @Singleton
    fun provideGithubRepository(githubDataSource: GithubDataSource): GithubRepository {
        return GithubRepository(githubDataSource)
    }
}