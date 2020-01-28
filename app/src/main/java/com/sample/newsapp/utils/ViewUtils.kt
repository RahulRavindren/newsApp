package com.sample.newsapp.utils

import android.os.Build
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.BindingAdapter
import com.google.android.material.snackbar.Snackbar
import com.sample.newsapp.db.entity.Source
import com.squareup.picasso.Picasso


fun showToast(view: View, message: String) {
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
        Toast.makeText(view.context, message, Toast.LENGTH_LONG).show()
    } else {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
    }
}


@BindingAdapter("bind:image")
fun bindImage(imageView: ImageView, url: String?) {
    if (url.isNullOrEmpty()) return
    Picasso.get()
        .load(url)
        .into(imageView)
}

@BindingAdapter("bind:source")
fun bindSource(textView: TextView, source: Source?) {
    source ?: return
    textView.text = source.name
}


