package com.sample.newsapp.di

import android.app.Application
import dagger.Component

@Component(modules = [AppModule::class])
interface ApplicationComponent {
    fun inject(app: Application)
}