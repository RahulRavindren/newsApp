package com.sample.newsapp.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "news_entity")
data class NewsEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val source: Source? = null,
    val author: String? = "",
    val title: String? = "",
    val description: String? = "",
    val url: String? = "",
    @ColumnInfo(name = "url_to_img") val urlToImage: String? = "",
    @ColumnInfo(name = "published_at") val publishedAt: String? = "",
    @ColumnInfo(name = "page_index") var pageIndex: Int = 0,
    val content: String? = ""
) : Serializable

data class Source(
    val id: String? = null,
    val name: String? = null
) : Serializable