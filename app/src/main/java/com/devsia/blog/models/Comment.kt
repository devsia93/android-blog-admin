package com.devsia.blog.models

data class Comment(
    val id: Int,
    val author_name: String,
    val text: String,
    val date_pub: String,
    val approved_comment: Boolean,
    val post: Int
    )