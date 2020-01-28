package com.sample.newsapp.di

import com.sample.newsapp.view.BaseListFragment
import dagger.Component

@Component(modules = [NewsFragmentModule::class])
interface NewsLisitingComponent {
    fun inject(fragment: BaseListFragment)
}