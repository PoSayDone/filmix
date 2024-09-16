package io.github.posaydone.filmix.di

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AppModule {
    @Provides
    @Singleton
    fun providePlayer(
        @ApplicationContext context: Context,
    ): ExoPlayer {
        return ExoPlayer.Builder(context).build()
    }

}
