package com.devsia.blog.models

data class Comment(
    val author: String,
    val text: String,
    val post: Post
)