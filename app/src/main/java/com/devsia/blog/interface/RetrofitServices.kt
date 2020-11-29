package com.devsia.blog.`interface`

import com.devsia.blog.models.Post
import retrofit2.Call
import retrofit2.http.GET

interface RetrofitServices {
    @GET("posts")
    fun getPostList(): Call<MutableList<Post>>
}