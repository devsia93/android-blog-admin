package com.devsia.blog.common

import com.devsia.blog.`interface`.RetrofitServices
import com.devsia.blog.retrofit.RetrofitClient

object Common {
    private const val BASE_URL = "http://83.220.175.143/blog/api/"
    val retrofitServices: RetrofitServices
        get() = RetrofitClient.getClient(BASE_URL).create(RetrofitServices::class.java)
}