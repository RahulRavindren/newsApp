package com.sample.newsapp.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sample.newsapp.R
import com.sample.newsapp.databinding.NewsItemBinding
import com.sample.newsapp.db.entity.NewsEntity
import com.sample.newsapp.view.onItemClick

val DIFF_CALLBACK = object : DiffUtil.ItemCallback<NewsEntity>() {
    override fun areItemsTheSame(oldItem: NewsEntity, newItem: NewsEntity): Boolean {
        return oldItem.title == newItem.title && oldItem.url == newItem.url
    }

    override fun areContentsTheSame(oldItem: NewsEntity, newItem: NewsEntity): Boolean {
        return oldItem == newItem
    }
}

class NewsPagerAdapter(
    context: Context,
    private val listener: onItemClick
) : PagedListAdapter<NewsEntity, NewsPagerAdapter.NewsViewHolder>(DIFF_CALLBACK) {
    val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding =
            DataBindingUtil.inflate<NewsItemBinding>(inflater, R.layout.news_item, parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class NewsViewHolder(private val binding: NewsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                listener(getItem(position), position)
            }
        }

        fun bind(item: NewsEntity?) {
            item ?: return
            binding.item = item
            binding.newsPos = position + 1
            binding.executePendingBindings()
        }

    }
}