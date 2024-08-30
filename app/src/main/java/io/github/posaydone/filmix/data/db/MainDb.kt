package io.github.posaydone.filmix.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import io.github.posaydone.filmix.data.dao.SeriesProgressDao
import io.github.posaydone.filmix.data.model.SeriesProgress

@Database (entities = [SeriesProgress::class], version = 1)
abstract class MainDb: RoomDatabase() {
    abstract fun getDao(): SeriesProgressDao

    companion object {
        fun getDb(context: Context) : MainDb {
            return databaseBuilder(
                context = context.applicationContext,
                klass = MainDb::class.java,
                name = "filmix.db"
            ).build()
        }
    }
}