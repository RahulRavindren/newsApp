package com.sample.newsapp.viewmodel

import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.core.os.bundleOf
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import androidx.paging.toLiveData
import androidx.savedstate.SavedStateRegistryOwner
import com.sample.newsapp.db.dao.TopHeadlines
import com.sample.newsapp.db.entity.NewsEntity
import com.sample.newsapp.domain.MediationUseCase
import com.sample.newsapp.domain.NewsUseCase
import com.sample.newsapp.domain.toMediator
import javax.inject.Inject

class NewsListViewModel(
    private val newsDao: TopHeadlines,
    val newsUC: MediationUseCase<Bundle, List<NewsEntity>>
) : ViewModel() {


    private val paginationCallback = object : PagedList.BoundaryCallback<NewsEntity>() {
        override fun onZeroItemsLoaded() {
            newsUC.execute(bundleOf(NewsUseCase.REFERESH_STATE to false))
        }

        override fun onItemAtEndLoaded(itemAtEnd: NewsEntity) {
            newsUC.execute(bundleOf(NewsUseCase.REFERESH_STATE to false))
        }
    }

    @VisibleForTesting
    fun getPGCallback(): PagedList.BoundaryCallback<NewsEntity> = paginationCallback

    fun paginationDF(): LiveData<PagedList<NewsEntity>> = newsDao.headlines()
        .toLiveData(
            config = PagedList.Config.Builder()
                .setPageSize(10)
                .setPrefetchDistance(3)
                .build(),
            boundaryCallback = paginationCallback
        )

    fun refresh() {
        newsUC.execute(bundleOf(NewsUseCase.REFERESH_STATE to true))
    }

    @Suppress("UNCHECKED_CAST")
    class Factory @Inject constructor(
        owner: SavedStateRegistryOwner,
        bundle: Bundle? = null,
        private val newsDao: TopHeadlines,
        private val newUC: NewsUseCase
    ) : AbstractSavedStateViewModelFactory(owner, bundle) {

        override fun <T : ViewModel?> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T = NewsListViewModel(newsDao, newUC.toMediator()) as T
    }

}