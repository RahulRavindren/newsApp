package com.sample.newsapp.di

import com.sample.newsapp.BuildConfig
import com.sample.newsapp.utils.ApiKeyInterceptor
import dagger.Module
import dagger.Provides
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
class NetworkModule {

    @Provides
    fun providesOKHttpClientBuilder(): OkHttpClient.Builder {
        return OkHttpClient.Builder()
            .addInterceptor(ApiKeyInterceptor())
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .callTimeout(60, TimeUnit.SECONDS)
    }

    @Provides
    fun providesRetrofit(clientBuilder: OkHttpClient.Builder): Retrofit.Builder {
        val baseUrl = HttpUrl.Builder()
            .host(BuildConfig.BASE_URL)
            .scheme("https")
            .build()
        return Retrofit.Builder()
            .client(clientBuilder.build())
            .baseUrl(baseUrl)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
            .addConverterFactory(GsonConverterFactory.create())
    }
}