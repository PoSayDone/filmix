package io.github.posaydone.filmix.app.host

import android.app.Application
import io.github.posaydone.filmix.data.network.client.FilmixApiClient
import io.github.posaydone.filmix.data.repository.FilmixRepository

class Filmix : Application() {

    // Create a repository instance scoped to this class
    lateinit var filmixRepository: FilmixRepository
        private set

    override fun onCreate() {
        super.onCreate()
        filmixRepository = FilmixRepository(FilmixApiClient().getApiService(this))
    }
}
