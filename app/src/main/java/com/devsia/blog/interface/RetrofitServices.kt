package com.devsia.blog.`interface`

import com.devsia.blog.models.Comment
import com.devsia.blog.models.Post
import com.devsia.blog.models.Tag
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitServices {
    @GET("posts/")
    fun getPostList(): Call<MutableList<Post>>

    @POST("posts/")
    fun postPost(@Header("Authorization") token: String, @Body body: RequestBody): Call<Post>?

    @GET("posts/{id}/")
    fun getPostById(@Path("id") id: Int): Call<Post>

    @PATCH("posts/{id}/")
    fun patchPostById(
        @Header("Authorization") token: String,
        @Body body: RequestBody,
        @Path("id") id: Int
    ): Call<Post>?

    @DELETE("posts/{id}/")
    fun deletePostById(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<ResponseBody>

    @GET("tags/{id}/posts/")
    fun getPostsByIdTag(@Path("id") id: Int): Call<MutableList<Post>>

    @GET("tags/")
    fun getTagsList(): Call<MutableList<Tag>>

    @GET("posts/{id}/comments/")
    fun getCommentsByPostId(@Path("id") id: Int): Call<MutableList<Comment>>

    @PATCH("comments/{id}/")
    fun patchCommentById(
        @Path("id") id: Int,
        @Header("Authorization") token: String,
        @Body body: RequestBody
    ): Call<Comment>?
}