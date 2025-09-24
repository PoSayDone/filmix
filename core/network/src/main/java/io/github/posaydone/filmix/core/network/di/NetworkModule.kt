package io.github.posaydone.filmix.core.network.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.posaydone.filmix.core.network.Constants
import io.github.posaydone.filmix.core.network.interceptor.SharedPreferencesCookieJar
import io.github.posaydone.filmix.core.network.service.AuthService
import io.github.posaydone.filmix.core.network.service.FilmixApiService
import okhttp3.CookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {
    @Provides
    @Singleton
    fun provideCookieJar(cookieJar: SharedPreferencesCookieJar): CookieJar {
        return cookieJar
    }

    @Provides
    fun provideOkHttpClient(
        cookieJar: CookieJar,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthService(
        cookieJar: CookieJar,
    ): AuthService {
        return Retrofit.Builder().baseUrl(Constants.BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create(
                )
            ).client(
                OkHttpClient.Builder().cookieJar(cookieJar)
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .build()
            ).build()
            .create(AuthService::class.java)
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
