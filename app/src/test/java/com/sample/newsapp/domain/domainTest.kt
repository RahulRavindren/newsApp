package com.sample.newsapp.domain

import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.os.bundleOf
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import com.sample.newsapp.domain.domainTest.ApiCall.Companion.BUNDLE_A
import com.sample.newsapp.domain.domainTest.BuildPayload.Companion.BUNDLE_S
import com.sample.newsapp.utils.TestUtils.getValue
import io.mockk.MockKAnnotations
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class domainTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    class BuildPayload : BundleUseCase<List<String>> {
        override fun invoke(p1: Bundle): Observable<List<String>> {
            return Observable.fromCallable { listOf(p1.getString(BUNDLE_S) ?: "", "aa") }
        }

        companion object {
            const val BUNDLE_S = "a"
        }
    }

    class ApiCall(val buildPayload: BuildPayload) : BundleUseCase<List<String>> {
        override fun invoke(p1: Bundle): Observable<List<String>> {
            return buildPayload(p1).map { p ->
                p.map {
                    "$it + ${p1.getString(BUNDLE_A)} + 42"
                }
            }
        }

        companion object {
            const val BUNDLE_A = "b"
        }
    }

    class DelayedIncUsecase : UseCase<Int, Int> {
        override fun invoke(p1: Int): Observable<Int> {
            return Observable.just(p1)
                .delay(1, TimeUnit.SECONDS)
                .map { it + 1 }
        }
    }


    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    @Test
    fun `composition check`() {
        val apiCall = ApiCall(BuildPayload())
        val toMediator = apiCall.toMediator()
        var cur: Result<List<String>> = Result.failure(Exception(""))
        toMediator.execute(bundleOf(BUNDLE_A to "baa", BUNDLE_S to "e"))
        toMediator.data().observeForever {
            cur = it
        }
        toMediator.execute(bundleOf(BUNDLE_A to "beee", BUNDLE_S to "fff"))
        Truth.assertThat(cur).isNotNull()
        Truth.assertThat(cur.isSuccess).isTrue()
        val list = cur.getOrNull()
        Truth.assertThat(list)
            .containsExactly("fff + beee + 42", "aa + beee + 42")
    }

    @Test
    fun `mediation test`() {
        val delayedIncUsecase = DelayedIncUsecase().toMediator<Int, Int>()
        val status = delayedIncUsecase.status()

        delayedIncUsecase.execute(1)

        Truth.assertThat(getValue(status, 0)).isTrue()
        Thread.sleep(500)
        Truth.assertThat(getValue(status, 0)).isTrue()
        Thread.sleep(600)
        Truth.assertThat(getValue(status, 0)).isFalse()
    }

    @Test
    fun `mediation ignore concurrent request based on flag`() {
        val delayedIncUsecase = DelayedIncUsecase().toMediator(true, Schedulers.io())
        delayedIncUsecase.execute(1)
        delayedIncUsecase.execute(2) // will be ignored
        delayedIncUsecase.execute(3)// will be ignored
        Truth.assertThat(getValue(delayedIncUsecase.data(), 0)).isNull()
        Thread.sleep(1059) // 1s passed. Must have posted and now it will take more events
        Truth.assertThat(getValue(delayedIncUsecase.data())).isEqualTo(Result.success(2))
        delayedIncUsecase.execute(4)
        Thread.sleep(1019)
        Truth.assertThat(getValue(delayedIncUsecase.data())).isEqualTo(Result.success(5))
    }
}