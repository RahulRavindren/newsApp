package com.sample.newsapp.utils

import com.sample.newsapp.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

/*
* Apply api_key as query params in the request
*
* */
class ApiKeyInterceptor : Interceptor {
    private val API_KEY = "apiKey"
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newUrl = request.url.newBuilder()
            .addQueryParameter(API_KEY, BuildConfig.API_KEY.trim())
            .build()
        return chain.proceed(request.newBuilder().url(newUrl).build())
    }
}