package io.github.posaydone.filmix.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.posaydone.filmix.data.model.SeriesProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface SeriesProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeriesProgress(item: SeriesProgress)
    @Query("SELECT * FROM series_progress")
    fun getAllSeriesProgress(): Flow<List<SeriesProgress>>
    @Query("SELECT * FROM series_progress WHERE id = :seriesId")
    fun getSeriesProgressById(seriesId: Int): Flow<SeriesProgress>
}