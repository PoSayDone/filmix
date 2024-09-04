package io.github.posaydone.filmix.data.api

import android.content.Context
import io.github.posaydone.filmix.data.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FilmixRefreshClient {
    private val TAG: String = "FilmixApiClient"
    private lateinit var apiService: FilmixRefreshService

    fun getApiService(context: Context): FilmixRefreshService {
        if (!::apiService.isInitialized) {
            val retrofit = Retrofit.Builder().baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            apiService = retrofit.create(FilmixRefreshService::class.java)
        }

        return apiService
    }
}
