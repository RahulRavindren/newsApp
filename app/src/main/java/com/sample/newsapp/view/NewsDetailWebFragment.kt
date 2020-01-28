package com.sample.newsapp.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sample.newsapp.R
import com.sample.newsapp.databinding.FragmentDetailWebBinding
import com.sample.newsapp.db.entity.NewsEntity

class NewsDetailWebFragment : Fragment() {
    var binding: FragmentDetailWebBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail_web, container, false)
        return binding?.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val entity = arguments?.getSerializable("newsEntity") as? NewsEntity
        super.onViewCreated(view, savedInstanceState)

        entity ?: return
        binding?.item = entity
        binding?.executePendingBindings()

        binding?.close?.setOnClickListener {
            activity?.let {
                val main = it as MainActivity
                main.navController.popBackStack()
            }
        }

        entity.url.let { url ->
            binding?.newsWeb?.webChromeClient = WebChromeClient()
            binding?.newsWeb?.loadUrl(url)
            val settings = binding?.newsWeb?.settings
            settings?.javaScriptEnabled = true
        }

    }

}