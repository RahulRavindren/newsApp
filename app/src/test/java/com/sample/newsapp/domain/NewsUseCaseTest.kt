package com.sample.newsapp.domain

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.os.bundleOf
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import com.sample.newsapp.db.dao.TopHeadlines
import com.sample.newsapp.db.entity.ApiResponse
import com.sample.newsapp.db.entity.NewsEntity
import com.sample.newsapp.domain.api.HeadlinesApi
import io.mockk.MockKAnnotations
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import io.reactivex.Observable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NewsUseCaseTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @MockK
    lateinit var dummyApi: HeadlinesApi
    @MockK
    lateinit var dummyHeadlineDao: TopHeadlines

    lateinit var newsUseCase: NewsUseCase


    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        newsUseCase = NewsUseCase(dummyHeadlineDao, dummyApi)
    }

    @Test
    fun `first page deleteAll() throws exception`() {
        every {
            dummyApi.getTopNews()
        } returns Observable.just(ApiResponse(articles = emptyList()))
        every {
            dummyHeadlineDao.deleteAll()
        } throws Exception()

        newsUseCase.invoke(bundleOf(NewsUseCase.REFERESH_STATE to true))
            .test()
            .assertError(Exception::class.java)

        verify {
            dummyApi.getTopNews()
            dummyHeadlineDao.deleteAll()
        }

        verify(exactly = 0) {
            dummyHeadlineDao.maxPageNum()
            dummyHeadlineDao.headlines()
        }

        confirmVerified(dummyApi, dummyHeadlineDao)
    }

    @Test
    fun `first page nextIndexPage num is -1`() {
        every {
            dummyApi.getTopNews()
        } returns Observable.just(ApiResponse(articles = dummyEntityList.toList()))
        every {
            dummyHeadlineDao.deleteAll()
        } returns Unit
        every {
            dummyHeadlineDao.maxPageNum()
        } returns -1

        every {
            dummyHeadlineDao.insReplace(dummyEntityList)
        } returns Unit

        newsUseCase.invoke(bundleOf(NewsUseCase.REFERESH_STATE to true))
            .test()
            .assertValue {
                it.onEach { Truth.assertThat(it.pageIndex == 1) ?: return@assertValue false }
                return@assertValue true
            }

    }

    @Test
    fun `next page fetch but encounter row count -1 observable should complete`() {
        every {
            dummyHeadlineDao.count()
        } returns -1
        newsUseCase.invoke(bundleOf(NewsUseCase.REFERESH_STATE to false))
            .test()
            .assertComplete()
            .assertNoValues()

        verify {
            dummyHeadlineDao.count()
        }
        confirmVerified(dummyHeadlineDao)
    }

    @Test
    fun `row count is zero execute fp case`() {
        every {
            dummyHeadlineDao.count()
        } returns 0
        every {
            dummyApi.getTopNews()
        } returns Observable.just(ApiResponse(articles = emptyList()))

        every {
            dummyHeadlineDao.deleteAll()
        } returns Unit

        every {
            dummyHeadlineDao.maxPageNum()
        } returns 0

        every {
            dummyHeadlineDao.insReplace(emptyList())
        } returns Unit

        newsUseCase.invoke(bundleOf(NewsUseCase.REFERESH_STATE to false))
            .test()

        verify {
            dummyHeadlineDao.count()
            dummyApi.getTopNews()
            dummyHeadlineDao.deleteAll()
            dummyHeadlineDao.maxPageNum()
            dummyHeadlineDao.insReplace(emptyList())
        }
        confirmVerified(dummyApi, dummyHeadlineDao)
    }

    @Test
    fun `next page fetch case`() {
        every {
            dummyHeadlineDao.count()
        } returns 10
        every {
            dummyHeadlineDao.maxPageNum()
        } returns 0
        every {
            dummyApi.getTopNews(page = 1)
        } returns Observable.just(ApiResponse(articles = dummyEntityList.toList()))

        every {
            dummyHeadlineDao.insReplace(dummyEntityList)
        } returns Unit

        newsUseCase.invoke(bundleOf(NewsUseCase.REFERESH_STATE to false))
            .test()
            .assertValue { it.all { it.pageIndex == 1 } }

        verify {
            dummyHeadlineDao.count()
            dummyHeadlineDao.maxPageNum()
            dummyApi.getTopNews(page = 1)
            dummyHeadlineDao.insReplace(dummyEntityList)
        }


    }

    private val dummyEntityList = arrayListOf<NewsEntity>(
        NewsEntity(title = "news1"),
        NewsEntity(title = "news2"),
        NewsEntity(title = "news3")
    )
}