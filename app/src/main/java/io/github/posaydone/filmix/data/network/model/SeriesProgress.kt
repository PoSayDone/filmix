package io.github.posaydone.filmix.data.network.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "series_progress")
data class SeriesProgress(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "season")
    val season: Int,
    @ColumnInfo(name = "episode")
    val episode: Int,
    @ColumnInfo(name = "translation")
    val translation: String,
    @ColumnInfo(name = "quality")
    val quality: Int,
)
