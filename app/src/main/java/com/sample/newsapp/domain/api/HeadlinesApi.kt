package com.sample.newsapp.domain.api

import com.sample.newsapp.db.entity.ApiResponse
import com.sample.newsapp.db.entity.NewsEntity
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface HeadlinesApi {

    @GET("/v2/top-headlines")
    fun getTopNews(
        @Query("country") country: String = "in",
        @Query("pageSize") pageSize: Int = 10,
        @Query("page") page: Int = 0
    ): Observable<ApiResponse<List<NewsEntity>>>
}