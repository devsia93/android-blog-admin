package com.devsia.blog.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.devsia.blog.R
import com.devsia.blog.`interface`.RetrofitServices
import com.devsia.blog.adapters.CommentListAdapter
import com.devsia.blog.common.Common
import com.devsia.blog.helper.Helper
import com.devsia.blog.helper.RecyclerTouchListener
import com.devsia.blog.models.Comment
import com.devsia.blog.models.CommentService
import com.devsia.blog.models.Post
import com.devsia.blog.preference.Const
import com.devsia.blog.preference.PreferenceHelper
import kotlinx.android.synthetic.main.activity_post.*
import kotlinx.android.synthetic.main.layout_post_content_view.*
import kotlinx.android.synthetic.main.layout_tags_content_view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable


class PostActivity : AppCompatActivity() {

    private lateinit var mService: RetrofitServices
    private lateinit var post: Post
    private lateinit var adapter: CommentListAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private lateinit var commentsService: MutableList<CommentService>
    private lateinit var selectedViews: MutableList<Boolean>
    private var countSelectedComments = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        mService = Common.retrofitServices
        handler = Handler(Looper.getMainLooper())
        post = intent.extras?.get(Const.Extra.extraPost()) as Post

        initViews()

        inflateViews()
    }

    private fun getAllCommentsList() {
        mService.getCommentsByPostId(post.id).enqueue(object : Callback<MutableList<Comment>> {
            override fun onResponse(
                call: Call<MutableList<Comment>>,
                response: Response<MutableList<Comment>>
            ) {
                val comments = response.body() as MutableList<Comment>
                commentsService = getConvertedComments(comments)
                adapter = CommentListAdapter(commentsService)
                adapter.setHasStableIds(true)
                adapter.notifyDataSetChanged()
                activity_post_rv_comments.adapter = adapter
            }

            private fun getConvertedComments(comments: MutableList<Comment>): MutableList<CommentService> {
                val result = mutableListOf<CommentService>()

                for (c in comments) {
                    val item = CommentService(
                        c.id,
                        c.author_name,
                        c.text,
                        c.date_pub,
                        c.approved_comment,
                        c.post
                    )
                    result.add(item)
                }

                return result
            }

            override fun onFailure(call: Call<MutableList<Comment>>, t: Throwable) {
                Log.e("retrofit-module", "comments bad request: $t")
            }
        })
    }

    private fun initViews() {
        layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        activity_post_rv_comments.layoutManager = layoutManager
        activity_post_rv_comments.setHasFixedSize(true)
        activity_post_rv_comments.addOnItemTouchListener(
            RecyclerTouchListener(
                applicationContext,
                activity_post_rv_comments,
                object : RecyclerTouchListener.ClickListener {
                    override fun onClick(view: View?, position: Int) {
                        if (countSelectedComments > 0) {
                            commonClick(position)
                            Helper.isTouched = true
                        }
                    }

                    override fun onLongClick(view: View?, position: Int) {
                        if (countSelectedComments == 0) {
                            selectedViews = MutableList(commentsService.size) { false }
                            invalidateOptionsMenu()
                            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_clear_24)
                        }
                        commonClick(position)
                    }

                    private fun commonClick(position: Int) {
                        if (selectedViews[position])
                            countSelectedComments--
                        else countSelectedComments++
                        selectedViews[position] = !selectedViews[position]
                        post_toolbar.title =
                            "$countSelectedComments item selected"

                        if (countSelectedComments == 0) {
                            invalidateOptionsMenu()
                            setDefaultToolbar()
                        }
                    }
                })
        )

        setDefaultToolbar()
        setSupportActionBar(post_toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        post_swipe_refresh.setOnRefreshListener {
            runnable = Runnable {
                requestToGetPostById()
                post_swipe_refresh.isRefreshing = false
            }

            handler.postDelayed(runnable, 2000)
        }
        post_swipe_refresh.setProgressBackgroundColorSchemeColor(
            Helper.getColorIdByAttr(
                R.attr.colorSecondary,
                theme
            )
        )
        post_swipe_refresh.setColorSchemeColors(
            Helper.getColorIdByAttr(
                R.attr.colorOnSecondary,
                theme
            )
        )
    }

    private fun setDefaultToolbar() {
        post_toolbar.title = "Post"
        post_toolbar.subtitle = post.slug
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        invalidateOptionsMenu()
    }

    private fun requestToGetPostById() {
        mService.getPostById(post.id).enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                post = response.body() as Post
                initViews()
                inflateViews()
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                Log.e("retrofit-module", "comments bad request: $t")
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.post_toolbar_menu, menu)
        menu?.setGroupVisible(R.id.post_comment_group, countSelectedComments > 0)
        menu?.setGroupVisible(R.id.post_default_group, countSelectedComments == 0)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if (countSelectedComments == 0) {
                    val intent = Intent(baseContext, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    setDefaultToolbar()
                    countSelectedComments = 0
                    adapter.clearSelectedItems()
                    adapter.notifyDataSetChanged()
                    selectedViews.clear()
                }
            }
            R.id.action_edit -> startEditPostActivity()
            R.id.action_delete -> {
                if (countSelectedComments == 0)
                    showDeletePostDialog()
                else showDeleteCommentsDialog()
            }
            R.id.action_disapprove_comment -> changeApproveStatusComment(false)
            R.id.action_approve_comment -> changeApproveStatusComment(true)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun changeApproveStatusComment(status: Boolean) {
        setDefaultToolbar()
        Helper.checkAvailableToken(baseContext)
        val token = PreferenceHelper.getToken(baseContext)
        if (token != null)
            for (comment in adapter.selectedCommentService)
                mService.patchCommentById(
                    comment.key.id,
                    token,
                    Helper.createJsonRequestBody("approved_comment" to status)
                )?.enqueue(object : Callback<Comment> {
                    override fun onResponse(
                        call: Call<Comment>,
                        response: Response<Comment>
                    ) {
                        val currentComment: CommentService? =
                            commentsService.find { it.id == (response.body() as Comment).id }
                        val indexCurrentComment = commentsService.indexOf(currentComment)
                        commentsService[indexCurrentComment].approved_comment = status
                        adapter.notifyItemChanged(indexCurrentComment)
                    }

                    override fun onFailure(call: Call<Comment>, t: Throwable) {
                        Log.e("retrofit-module", "approve comment bad request $t")
                    }
                })
        countSelectedComments = 0
        adapter.selectedCommentService.clear()
    }

    private fun startEditPostActivity() {
        val intent = Intent(baseContext, EditPostActivity::class.java)
        intent.putExtra(Const.Extra.extraPost(), post)
        startActivity(intent)
    }

    private fun showDeleteCommentsDialog() {
        Helper.checkAvailableToken(this)
        val token = PreferenceHelper.getToken(this)
        if (!token.isNullOrEmpty())
            AlertDialog.Builder(this)
                .setMessage("Please, confirm the delete $countSelectedComments comments.")
                .setPositiveButton("Confirm") { _, _ ->
                    deleteCommentsById(token)
                }
                .setNegativeButton("Cancel", null)
                .create()
                .show()
    }

    private fun deleteCommentsById(token: String?) {
        for (comment in adapter.selectedCommentService)
            mService.deleteCommentById(token!!, comment.key.id)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        val currentComment: CommentService? =
                            commentsService.find { it.id == comment.key.id }
                        val indexCurrentComment = commentsService.indexOf(currentComment)
                        commentsService.removeAt(indexCurrentComment)
                        adapter.notifyItemChanged(indexCurrentComment)
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.e("retrofit-module", "delete comment bad request $t")
                    }
                })
    }

    private fun showDeletePostDialog() {
        Helper.checkAvailableToken(this)

        if (!PreferenceHelper.getToken(this).isNullOrEmpty())
            AlertDialog.Builder(this)
                .setMessage("Please, confirm the delete this post.")
                .setPositiveButton("Confirm") { _, _ ->
                    deletePostById()
                }
                .setNegativeButton("Cancel", null)
                .create()
                .show()
    }

    private fun deletePostById() {
        mService.deletePostById(PreferenceHelper.getToken(this)!!, post.id)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    intent = Intent(baseContext, MainActivity::class.java)
                    startActivity(intent)
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("retrofit-module", "delete bad request $t")
                }
            })
    }

    private fun inflateViews() {
        post_content_tv_title.text = post.title
        post_content_tv_body.text = post.body
        tags_content_main_layout.isVisible = !post.tags.isNullOrEmpty()

        if (!post.tags.isNullOrEmpty()) {
            val tags = PreferenceHelper.getListTags(this)

            for (tagId in post.tags!!) {
                for (tag in tags) {
                    if (tag.id == tagId) {
                        val chip = Helper.getCreatedChip(tags_content_fb_view, tag)

                        if (chip != null) {
                            tags_content_fb_view.addView(chip)

                            chip.setOnClickListener {
                                val intent = Intent(it.context, MainActivity::class.java)
                                intent.putExtra(Const.Extra.extraTag(), tag as Serializable)
                                it.context.startActivity(intent)
                            }
                        }
                    }
                }
            }
        }

        if (post.comment_count>0) {
            mService = Common.retrofitServices
            activity_post_tv_comments.isVisible = true
            getAllCommentsList()
        }
    }
}