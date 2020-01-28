package com.sample.newsapp.domain

import android.os.Bundle
import androidx.core.os.bundleOf
import com.sample.newsapp.db.dao.TopHeadlines
import com.sample.newsapp.db.entity.NewsEntity
import com.sample.newsapp.domain.api.HeadlinesApi
import io.reactivex.Observable
import java.io.Serializable
import javax.inject.Inject

/*
* Usecase of first page & next page fetch with Api. And saving into DB
* */
class NewsUseCase @Inject constructor(
    private val headlinesDao: TopHeadlines,
    private val api: HeadlinesApi
) : BundleUseCase<List<NewsEntity>> {

    private val TAG = NewsUseCase::class.java.simpleName

    companion object {
        val REFERESH_STATE = "REFERESH_STATE"
        val PAGE_TYPE = "PAGE_TYPE"
    }


    override fun invoke(p1: Bundle): Observable<List<NewsEntity>> {
        val isFresh = p1.getBoolean(REFERESH_STATE)
        val pageType =
            p1.getSerializable(PAGE_TYPE) ?: com.sample.newsapp.domain.PAGE_TYPE.FIRST_PAGE

        if (isFresh && pageType == com.sample.newsapp.domain.PAGE_TYPE.FIRST_PAGE) {
            //remove from db and make network call
            return api.getTopNews()
                .map { response ->
                    headlinesDao.deleteAll()
                    var nextIndex = headlinesDao.maxPageNum()
                    if (nextIndex > 0) throw Exception("next_index for FP should be always 0")
                    nextIndex = if (nextIndex == -1) 0 else nextIndex
                    headlinesDao.insReplace(response.articles?.onEach { it.pageIndex = nextIndex })
                    response.articles
                }
        }
        //check row count again in DB
        return Observable.fromCallable { headlinesDao.count() }
            .filter { count -> count > -1 }
            .flatMap { count ->
                // nothing in DB. Go for first page fetch
                if (count == 0) invoke(
                    bundleOf(
                        NewsUseCase.REFERESH_STATE to true,
                        NewsUseCase.PAGE_TYPE to com.sample.newsapp.domain.PAGE_TYPE.FIRST_PAGE
                    )
                )
                else {
                    // next page fetch
                    val nextIndex = headlinesDao.maxPageNum()
                    api.getTopNews(page = nextIndex + 1)
                        .filter { it.articles != null && it.articles.isNotEmpty() }
                        .map { respone ->
                            headlinesDao.insReplace(respone.articles?.onEach { item ->
                                item.pageIndex = nextIndex + 1
                            })
                            respone.articles
                        }
                }
            }

    }
}

enum class PAGE_TYPE : Serializable {
    FIRST_PAGE, NEXT_PAGE
}