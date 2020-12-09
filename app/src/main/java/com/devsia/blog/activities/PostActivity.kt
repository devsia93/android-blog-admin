package com.devsia.blog.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.devsia.blog.R
import com.devsia.blog.`interface`.RetrofitServices
import com.devsia.blog.adapters.CommentListAdapter
import com.devsia.blog.common.Common
import com.devsia.blog.helper.Helper
import com.devsia.blog.models.Comment
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
                adapter = CommentListAdapter(response.body() as MutableList<Comment>)
                adapter.setHasStableIds(true)
                adapter.notifyDataSetChanged()
                activity_post_rv_comments.adapter = adapter
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

        post_toolbar.title = "Post"
        post_toolbar.subtitle = post.slug
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

    private fun requestToGetPostById() {
        mService.getPostById(post.id).enqueue(object : Callback<Post>{
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
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(baseContext, MainActivity::class.java)
                startActivity(intent)
            }
            R.id.action_edit -> {
                val intent = Intent(baseContext, EditPostActivity::class.java)
                intent.putExtra(Const.Extra.extraPost(), post)
                startActivity(intent)
            }
            R.id.action_delete -> {
                deletePost()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deletePost() {
        Helper.checkAvailableToken(this)

        if (!PreferenceHelper.getToken(this).isNullOrEmpty())
            AlertDialog.Builder(this)
                .setMessage("Please, confirm the delete")
                .setPositiveButton("Confirm") { _, _ ->
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
                .setNegativeButton("Cancel", null)
                .create()
                .show()
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

        if (!post.comments.isNullOrEmpty()) {
            mService = Common.retrofitServices
            activity_post_tv_comments.isVisible = true
            getAllCommentsList()
        }
    }
}