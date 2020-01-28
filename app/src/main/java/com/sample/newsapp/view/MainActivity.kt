package com.sample.newsapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import com.sample.newsapp.R

class MainActivity : AppCompatActivity() {
    private val NAV_STATE = "NAV_STATE"

    internal val navController by lazy {
        findNavController(R.id.fragment_container)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBundle(NAV_STATE, navController.saveState())
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        val bundle = savedInstanceState.getBundle(NAV_STATE)
        navController.restoreState(bundle)
        super.onRestoreInstanceState(savedInstanceState)
    }
}
