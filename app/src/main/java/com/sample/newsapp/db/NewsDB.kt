package com.sample.newsapp.db

import android.content.Context
import androidx.room.*
import com.google.gson.Gson
import com.sample.newsapp.db.dao.TopHeadlines
import com.sample.newsapp.db.entity.NewsEntity
import com.sample.newsapp.db.entity.Source

@Database(
    entities = [NewsEntity::class],
    version = 1
)
@TypeConverters(NewsDBTypeConverters::class)
abstract class NewsDB : RoomDatabase() {

    abstract fun topHeadlinesDao(): TopHeadlines

    companion object {
        private var INSTANCE: NewsDB? = null

        fun ins(context: Context, inMem: Boolean = false): NewsDB? {
            if (INSTANCE == null) {
                synchronized(this) {
                    if (INSTANCE == null) {
                        if (inMem) {
                            INSTANCE = Room.inMemoryDatabaseBuilder(context, NewsDB::class.java)
                                .allowMainThreadQueries()
                                .build()
                        } else {
                            INSTANCE = Room.databaseBuilder(context, NewsDB::class.java, "news.db")
                                .allowMainThreadQueries()
                                .build()
                        }
                    }
                }
            }
            return INSTANCE
        }
    }
}


class NewsDBTypeConverters {

    @TypeConverter
    fun fromSource(source: Source): String = Gson().toJson(source)

    @TypeConverter
    fun toSource(source: String): Source = Gson().fromJson(source, Source::class.java)

}