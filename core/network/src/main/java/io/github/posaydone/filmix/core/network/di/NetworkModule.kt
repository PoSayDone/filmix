package io.github.posaydone.filmix.core.network.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.posaydone.filmix.core.network.Constants
import io.github.posaydone.filmix.core.network.interceptor.AuthInterceptor
import io.github.posaydone.filmix.core.network.interceptor.TokenAuthenticator
import io.github.posaydone.filmix.core.network.service.AuthService
import io.github.posaydone.filmix.core.network.service.FilmixApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {
    @Provides
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor(
                authInterceptor
            ).authenticator(tokenAuthenticator).build()

    }

    @Provides
    @Singleton
    fun provideAuthService(): AuthService {
        return Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(
                GsonConverterFactory.create(
                )
            ).client(
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .build()
            ).build().create(AuthService::class.java)
    }

    @Provides
    @Singleton
    fun provideFilmixApiService(
        okHttpClient: OkHttpClient,
    ): FilmixApiService {
        return Retrofit.Builder().baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build()
            .create(FilmixApiService::class.java)
    }
}
