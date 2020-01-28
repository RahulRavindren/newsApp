package com.sample.newsapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.idling.CountingIdlingResource
import com.sample.newsapp.R
import com.sample.newsapp.databinding.FragmentBaseListingBinding
import com.sample.newsapp.db.NewsDB
import com.sample.newsapp.db.entity.NewsEntity
import com.sample.newsapp.di.DaggerNewsLisitingComponent
import com.sample.newsapp.di.NetworkModule
import com.sample.newsapp.di.NewsFragmentModule
import com.sample.newsapp.utils.showToast
import com.sample.newsapp.view.adapter.NewsPagerAdapter
import com.sample.newsapp.viewmodel.NewsListViewModel
import javax.inject.Inject

open class BaseListFragment : Fragment() {

    open fun entityType(): EntityType = EntityType.HEADLINES

    private lateinit var mBinding: FragmentBaseListingBinding

    @Inject
    lateinit var VMFactory: NewsListViewModel.Factory
    private lateinit var vm: NewsListViewModel
    private var adapter: NewsPagerAdapter? = null

    private var idlingResource: CountingIdlingResource? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerNewsLisitingComponent.builder()
            .newsFragmentModule(
                NewsFragmentModule(
                    this,
                    savedInstanceState, NewsDB.ins(context!!)!!
                )
            )
            .networkModule(NetworkModule())
            .build().inject(this)

        vm = ViewModelProvider(this, VMFactory)[NewsListViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_base_listing, container, false)
        return mBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.swipeRefresh.isRefreshing = true
        mBinding.swipeRefresh.setOnRefreshListener {
            vm.refresh()
        }
        initList()

        //pagedlist for observing from datasource factory of DB
        vm.paginationDF().observe(viewLifecycleOwner, Observer {
            mBinding.swipeRefresh.isRefreshing = false
            adapter?.submitList(it)
            idlingResource?.decrement()
        })


        // subscribe to usecase results
        vm.newsUC.data().observe(viewLifecycleOwner, Observer {
            //need to only observer failure case here
            if (it.isSuccess) {

            } else {
                mBinding.swipeRefresh.isRefreshing = false
                showToast(mBinding.root, "Something went wrong. Please try again")
            }
            idlingResource?.decrement()
        })
        idlingResource?.increment()
    }

    private fun initList() {
        activity?.let {
            adapter = NewsPagerAdapter(context!!) { item: NewsEntity?, _: Int ->
                //navigate to detail fragment
                val main = it as MainActivity
                item ?: return@NewsPagerAdapter
                val action = BaseListFragmentDirections.clickDetailAction(item)
                main.navController.navigate(action)
            }
        }
        mBinding.listing.adapter = adapter
        mBinding.listing.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    }


    @VisibleForTesting
    internal fun getIdlingResource(): IdlingResource? {
        if (idlingResource == null) {
            idlingResource = CountingIdlingResource("listing_fragment", true)
        }
        return idlingResource
    }


}

//listener for item click in recyclerview
typealias onItemClick = (NewsEntity?, Int) -> Unit

enum class EntityType {
    HEADLINES, ALL
}