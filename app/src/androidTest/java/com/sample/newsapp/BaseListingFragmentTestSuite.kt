package com.sample.newsapp

import androidx.fragment.app.testing.FragmentScenario
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.sample.newsapp.view.BaseListFragment
import com.sample.newsapp.view.adapter.NewsPagerAdapter
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


//TODO: complete UI tests
@RunWith(AndroidJUnit4::class)
@LargeTest
class BaseListingFragmentTestSuite {
    lateinit var scenario: FragmentScenario<BaseListFragment>
    internal var idlingResource: IdlingResource? = null

    @Before
    fun setUp() {
        scenario = FragmentScenario.launchInContainer(BaseListFragment::class.java)
        scenario.onFragment {
            idlingResource = it.getIdlingResource()
            IdlingRegistry.getInstance().register(idlingResource)
        }
    }

    @Test
    fun checkItemsInList() {
        scenario.onFragment {
            val recyclerView = it.view?.findViewById<RecyclerView>(R.id.listing)
            val count = recyclerView?.adapter?.itemCount ?: 0
            if (count > 0) {
                onView(ViewMatchers.withId(R.id.listing)).perform(
                    RecyclerViewActions.actionOnItemAtPosition<NewsPagerAdapter.NewsViewHolder>(
                        0,
                        ViewActions.click()
                    )
                )
            }
        }
    }

    @After
    fun tearDown() {
        idlingResource?.let {
            IdlingRegistry.getInstance().unregister(idlingResource)
        }
    }
}