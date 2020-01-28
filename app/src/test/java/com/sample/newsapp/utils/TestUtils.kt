package com.sample.newsapp.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

object TestUtils {

    @Throws(InterruptedException::class)
    fun <T> getValue(liveData: LiveData<T>, sec: Long = 2): T? {
        var data: T? = null
        val latch = CountDownLatch(1)
        val observer = object : Observer<T> {
            override fun onChanged(o: T?) {
                data = o
                latch.countDown()
                liveData.removeObserver(this)
            }
        }
        liveData.observeForever(observer)
        latch.await(sec, TimeUnit.SECONDS)

        return data
    }
}