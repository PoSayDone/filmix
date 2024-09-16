package io.github.posaydone.filmix.core.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.posaydone.filmix.core.data.FilmixRepository
import io.github.posaydone.filmix.core.data.SessionManagerImpl
import io.github.posaydone.filmix.core.model.SessionManager
import io.github.posaydone.filmix.core.network.FilmixRemoteDataSource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideFilmixRepository(
        apiService: FilmixRemoteDataSource,
    ): FilmixRepository {
        return FilmixRepository(apiService)
    }


    @Provides
    @Singleton
    fun provideSessionManager(
        @ApplicationContext context: Context,
    ): SessionManager {
        return SessionManagerImpl(context)
    }
}
