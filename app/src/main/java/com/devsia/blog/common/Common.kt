package com.devsia.blog.common

import com.devsia.blog.`interface`.RetrofitServices
import com.devsia.blog.retrofit.RetrofitClient

object Common {
    private val BASE_URL = "http://jsonplaceholder.typicode.com/"
    val retrofitServices: RetrofitServices
        get() = RetrofitClient.getClient(BASE_URL).create(RetrofitServices::class.java)
}