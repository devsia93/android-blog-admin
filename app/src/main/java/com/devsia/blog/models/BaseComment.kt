package com.devsia.blog.models

abstract class BaseComment {
    abstract val id: Int
    abstract val author_name: String
    abstract val text: String
    abstract val date_pub: String
    abstract val approved_comment: Boolean
    abstract val post: Int
}