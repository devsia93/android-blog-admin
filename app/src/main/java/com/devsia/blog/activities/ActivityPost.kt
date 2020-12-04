package com.devsia.blog.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.devsia.blog.R
import com.devsia.blog.`interface`.RetrofitServices
import com.devsia.blog.common.Common
import com.devsia.blog.helper.Helper
import com.devsia.blog.models.Comment
import com.devsia.blog.models.Post
import com.devsia.blog.preference.Const
import com.devsia.blog.preference.PreferenceHelper
import kotlinx.android.synthetic.main.layout_post_content_view.*
import kotlinx.android.synthetic.main.layout_tags_content_view.*

class ActivityPost : AppCompatActivity() {

    private lateinit var mService: RetrofitServices
    private lateinit var post: Post

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        post = intent.extras?.get(Const.Extra.extraPost()) as Post

        inflateViews()
    }

    private fun inflateViews() {
        post_content_tv_title.text = post.title
        post_content_tv_body.text = post.body
        tags_content_main_layout?.isVisible = !post.tags.isNullOrEmpty()

        if (!post.tags.isNullOrEmpty()) {
            val pref = PreferenceHelper(baseContext)
            val tags = pref.getListTags()

            for (tagId in post.tags!!) {
                for (tag in tags) {
                    if (tag.id == tagId)
                        Helper.createChip(tags_content_fb_view, tag.title)
                }
            }
        }

        if (!post.comments.isNullOrEmpty()) {
            mService = Common.retrofitServices

            for (commentId in post.comments!!) {
                val comment: Comment = mService.getCommentById(commentId)

            }
        }
    }
}