package com.sample.newsapp.di

import android.os.Bundle
import androidx.savedstate.SavedStateRegistryOwner
import com.sample.newsapp.db.NewsDB
import com.sample.newsapp.db.dao.TopHeadlines
import com.sample.newsapp.domain.NewsUseCase
import com.sample.newsapp.domain.api.HeadlinesApi
import com.sample.newsapp.viewmodel.NewsListViewModel
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit


@Module(includes = [NetworkModule::class])
class NewsFragmentModule(
    private val savedStateOwner: SavedStateRegistryOwner,
    private val args: Bundle? = null,
    private val db: NewsDB
) {
    @Provides
    fun providesVMFactory(
        newsDao: TopHeadlines,
        newsUseCase: NewsUseCase
    ): NewsListViewModel.Factory {
        return NewsListViewModel.Factory(savedStateOwner, args, newsDao, newsUseCase)
    }

    @Provides
    fun providesToHeadlinesDao(): TopHeadlines = db.topHeadlinesDao()

    @Provides
    fun providesNewsUseCase(newsDao: TopHeadlines, api: HeadlinesApi): NewsUseCase {
        return NewsUseCase(newsDao, api)
    }

    @Provides
    fun providesHeadlinesApi(retrofitBuilder: Retrofit.Builder): HeadlinesApi {
        return retrofitBuilder.build().create(HeadlinesApi::class.java)
    }
}