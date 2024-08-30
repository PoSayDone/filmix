package io.github.posaydone.filmix.data.repository

import io.github.posaydone.filmix.data.dao.SeriesProgressDao
import io.github.posaydone.filmix.data.model.SeriesProgress
import kotlinx.coroutines.flow.Flow

class SeriesProgressRepository(private val seriesProgressDao: SeriesProgressDao) {

    // Function to insert a SeriesProgress item into the database
    suspend fun insertSeriesProgress(item: SeriesProgress) {
        seriesProgressDao.insertSeriesProgress(item)
    }

    // Function to get all SeriesProgress items from the database
    fun getAllSeriesProgress(): Flow<List<SeriesProgress>> {
        return seriesProgressDao.getAllSeriesProgress()
    }

    // Function to get a specific SeriesProgress item by its ID
    fun getSeriesProgressById(seriesId: Int): Flow<SeriesProgress> {
        return seriesProgressDao.getSeriesProgressById(seriesId)
    }
}
