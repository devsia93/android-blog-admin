package com.devsia.blog.models

import java.io.Serializable

data class Post(
    val id: Int,
    val slug: String,
    val title: String,
    val body: String,
    val tags: List<Int>? = null,
    val date_pub: String,
    val comments: List<Int>? = null,
) : Serializable