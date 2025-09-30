package io.github.posaydone.filmix.core.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.posaydone.filmix.core.data.repository.AuthRepository
import io.github.posaydone.filmix.core.data.repository.FilmixRepository
import io.github.posaydone.filmix.core.data.repository.KinopoiskRepository
import io.github.posaydone.filmix.core.data.SessionManagerImpl
import io.github.posaydone.filmix.core.data.repository.GithubRepository
import io.github.posaydone.filmix.core.model.SessionManager
import io.github.posaydone.filmix.core.network.dataSource.AuthRemoteDataSource
import io.github.posaydone.filmix.core.network.dataSource.FilmixRemoteDataSource
import io.github.posaydone.filmix.core.network.dataSource.KinopoiskRemoteDataSource
import io.github.posaydone.filmix.core.network.dataSource.GithubDataSource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideFilmixRepository(
        dataSource: FilmixRemoteDataSource,
    ): FilmixRepository {
        return FilmixRepository(dataSource)
    }

    @Provides
    @Singleton
    fun provideGithubRepository(
        dataSource: GithubDataSource,
    ): GithubRepository {
        return GithubRepository(dataSource)
    }

    @Provides
    @Singleton
    fun provideKinopoiskRepository(
        dataSource: KinopoiskRemoteDataSource,
    ): KinopoiskRepository {
        return KinopoiskRepository(dataSource)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        dataSource: AuthRemoteDataSource,
    ): AuthRepository {
        return AuthRepository(dataSource)
    }

    @Provides
    @Singleton
    fun provideSessionManager(
        @ApplicationContext context: Context,
    ): SessionManager {
        return SessionManagerImpl(context)
    }
}
