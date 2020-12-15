package com.devsia.blog.models

class CommentService(
    override val id: Int,
    override val author_name: String,
    override val text: String,
    override val date_pub: String,
    override var approved_comment: Boolean,
    override val post: Int,
    private var isSelected: Boolean = false
) : BaseComment() {

    fun setSelected(status: Boolean) {
        isSelected = status
    }

    fun getSelected() = isSelected
}
