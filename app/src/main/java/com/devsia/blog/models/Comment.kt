package com.devsia.blog.models

data class Comment(
    override val id: Int,
    override val author_name: String,
    override val text: String,
    override val date_pub: String,
    override val approved_comment: Boolean,
    override val post: Int
) : BaseComment()