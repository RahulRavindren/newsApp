package com.sample.newsapp.db.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import com.sample.newsapp.db.entity.NewsEntity

@Dao
abstract class TopHeadlines : BaseDao<NewsEntity> {
    //datasource for pagedlist builder
    @Query("SELECT * from news_entity ORDER BY page_index ASC")
    abstract fun headlines(): DataSource.Factory<Int, NewsEntity>

    //count no. of rows in db
    @Query("SELECT COUNT(*) from news_entity")
    abstract fun count(): Int

    //delete all rows
    @Query("DELETE FROM news_entity")
    abstract fun deleteAll()

    // max page_index of existing rows for fetch of next_page
    @Query("SELECT MAX(page_index) FROM news_entity")
    abstract fun maxPageNum(): Int
}