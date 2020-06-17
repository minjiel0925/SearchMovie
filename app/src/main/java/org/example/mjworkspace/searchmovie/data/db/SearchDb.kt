package org.example.mjworkspace.searchmovie.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.example.mjworkspace.searchmovie.data.model.SearchResult

@Database(
    entities = [SearchResult::class],
    version = 1,
    exportSchema = false
)
abstract class SearchDb : RoomDatabase() {
    abstract fun searchResultDao() : SearchResultDao

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: SearchDb? = null

        fun getInstance(context: Context): SearchDb {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it}
            }
        }

        private fun buildDatabase(context: Context) : SearchDb {
            return Room
                .databaseBuilder(context, SearchDb::class.java, "searchdb")
                .build()
        }
    }

}