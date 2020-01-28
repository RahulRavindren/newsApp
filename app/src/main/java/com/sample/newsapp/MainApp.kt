package com.sample.newsapp

import android.app.Application
import com.sample.newsapp.di.AppModule
import com.sample.newsapp.di.DaggerApplicationComponent

class MainApp : Application() {

    override fun onCreate() {
        super.onCreate()
        DaggerApplicationComponent.builder()
            .appModule(AppModule(this))
            .build().inject(this)
    }
}