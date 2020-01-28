package com.sample.newsapp.db.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy

interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insReplace(vararg item: T?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insReplace(items: List<T>?)

}