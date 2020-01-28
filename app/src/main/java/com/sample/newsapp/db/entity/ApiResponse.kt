package com.sample.newsapp.db.entity

import java.io.Serializable

data class ApiResponse<T>(
    val status: String = "",
    val code: String = "",
    val articles: T? = null,
    val message: String = ""
) : Serializable