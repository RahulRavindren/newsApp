package com.sample.newsapp.interceptors

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import com.sample.newsapp.BuildConfig
import com.sample.newsapp.domain.api.HeadlinesApi
import com.sample.newsapp.utils.ApiKeyInterceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ApiKeyInterceptorTest {
    lateinit var server: MockWebServer
    lateinit var dummyApi: HeadlinesApi
    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
    }

    @Test
    fun `check if api key added to request params`() {
        server.enqueue(MockResponse())
        val client = OkHttpClient.Builder()
            .addInterceptor(ApiKeyInterceptor()).build()
        client.newCall(Request.Builder().url(server.url("/")).build()).execute()

        val recordRequest = server.takeRequest()
        val queryParamValue = recordRequest.requestUrl?.queryParameter("apiKey") ?: ""
        Truth.assertThat(queryParamValue.equals(BuildConfig.API_KEY))
        server.shutdown()
    }
}