package com.devsia.blog.`interface`

import com.devsia.blog.models.Comment
import com.devsia.blog.models.Post
import com.devsia.blog.models.Tag
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface RetrofitServices {
    @GET("posts/")
    fun getPostList(): Call<MutableList<Post>>

    @GET("tags/")
    fun getTagsList(): Call<MutableList<Tag>>

    @GET("comments/{id}")
    fun getCommentById(@Path("id") id: Int): Comment
}