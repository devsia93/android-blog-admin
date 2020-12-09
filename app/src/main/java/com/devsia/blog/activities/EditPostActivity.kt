package com.devsia.blog.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.devsia.blog.R
import com.devsia.blog.`interface`.RetrofitServices
import com.devsia.blog.common.Common
import com.devsia.blog.helper.Helper
import com.devsia.blog.models.Post
import com.devsia.blog.models.Tag
import com.devsia.blog.preference.Const
import com.devsia.blog.preference.PreferenceHelper
import kotlinx.android.synthetic.main.activity_edit_post.*
import kotlinx.android.synthetic.main.layout_tags_content_view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditPostActivity : AppCompatActivity() {

    private lateinit var mService: RetrofitServices
    private var isEditable = false
    private var postExtra: Post? = null
    private var tagsList = mutableListOf<Tag>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_post)

        mService = Common.retrofitServices

        postExtra = intent?.extras?.get(Const.Extra.extraPost()) as Post?
        isEditable = postExtra != null

        if (isEditable)
            initViewsByPost()
        else initViews()
    }

    private fun initViewsByPost() {
        edit_post_toolbar.title = "Edit"
        edit_post_toolbar.subtitle = postExtra!!.slug
        setSupportActionBar(edit_post_toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        for (tag in PreferenceHelper.getListTags(this)) {
            var isChecked = false
            if (tag.id in postExtra!!.tags!!.toIntArray()) {
                isChecked = true
                tagsList.add(tag)
            }
            createChip(tag, isChecked)
        }

        edit_post_et_title.setText(postExtra!!.title)
        edit_post_et_body.setText(postExtra!!.body)
    }

    private fun initViews() {
        edit_post_toolbar.title = "Create"
        edit_post_toolbar.subtitle = "Post"
        setSupportActionBar(edit_post_toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        for (tag in PreferenceHelper.getListTags(this))
            createChip(tag)
    }

    private fun createChip(tag: Tag, isCheck: Boolean = false) {
        val chip = Helper.getCreatedChip(
            tags_content_fb_view, tag, isCheckable = true, isChecked = isCheck
        )
        tags_content_fb_view.addView(chip)
        chip?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                tagsList.add(tag)
            else tagsList.remove(tag)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_post_toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_send -> {
                if (isEditable)
                    patchToEdit()
                else postToCreate()
            }
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun postToCreate() {
        Helper.checkAvailableToken(this)
        val token = PreferenceHelper.getToken(this)
        val (title, body, tags) = triple()
        if (!token.isNullOrEmpty())
            mService.postPost(token, Helper.createJsonRequestBody(title, body, tags))
                ?.enqueue(object : Callback<Post> {
                    override fun onResponse(call: Call<Post>, response: Response<Post>) {
                        startActivityWithPost(response.body() as Post)
                    }

                    override fun onFailure(call: Call<Post>, t: Throwable) {
                        Log.e("retrofit-module", "[post] post bad request: $t")
                    }
                })
    }

    private fun startActivityWithPost(post: Post) {
        val intent = Intent(baseContext, PostActivity::class.java)
        intent.putExtra(Const.Extra.extraPost(), post)
        startActivity(intent)
    }

    private fun triple(): Triple<Pair<String, String>, Pair<String, String>, Pair<String, MutableList<Int>>> {
        val title = ("title" to edit_post_et_title.text.toString())
        val body = ("body" to edit_post_et_body.text.toString())

        val tagsId = mutableListOf<Int>()
        for (tag in tagsList)
            tagsId.add(tag.id)
        val tags = ("tags" to tagsId)
        return Triple(title, body, tags)
    }

    private fun patchToEdit() {
        Helper.checkAvailableToken(this)
        val token = PreferenceHelper.getToken(this)
        val (title, body, tags) = triple()
        if (!token.isNullOrEmpty())
            mService.patchPostById(
                token,
                Helper.createJsonRequestBody(title, body, tags),
                postExtra!!.id
            )?.enqueue(object : Callback<Post> {
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    startActivityWithPost(response.body() as Post)
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    Log.e("retrofit-module", "patch post bad request: $t")
                }
            })
    }

    override fun onBackPressed() {
        startActivityWithPost(postExtra!!)
    }
}