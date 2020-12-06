package com.devsia.blog.models

import java.io.Serializable

data class Tag(
    val title: String,
    val slug: String,
    val id: Int
) : Serializable