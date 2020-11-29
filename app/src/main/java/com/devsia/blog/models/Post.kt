package com.devsia.blog.models

data class Post(
    val body: String,
    val title: String,
    val tags: List<Tag>? = null,
    val slug: String?,
    val date_pub: String,
    val id: Int
)