package com.devsia.blog.activities

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.devsia.blog.R
import com.devsia.blog.`interface`.RetrofitServices
import com.devsia.blog.adapters.PostListAdapter
import com.devsia.blog.common.Common
import com.devsia.blog.models.Post
import com.devsia.blog.models.Tag
import com.devsia.blog.preference.PreferenceHelper
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var mService: RetrofitServices
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: PostListAdapter
    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mService = Common.retrofitServices

        main_rv_posts_content.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        main_rv_posts_content.layoutManager = layoutManager
        dialog = SpotsDialog.Builder().setCancelable(true).setContext(this).setTheme(R.style.Custom)
            .build()

        getAllTagsList()
        getAllPostsList()
    }

    private fun getAllTagsList() {
        dialog.show()
        mService.getTagsList().enqueue(object : Callback<MutableList<Tag>> {
            override fun onFailure(call: Call<MutableList<Tag>>, t: Throwable) {
                Log.e("retrofit-module", "tags bad request: $t")

            }

            override fun onResponse(
                call: Call<MutableList<Tag>>,
                response: Response<MutableList<Tag>>
            ) {
                val pref = PreferenceHelper(baseContext)
                pref.saveListTags(response.body() as List<Tag>)
            }
        })
    }

    private fun getAllPostsList() {
        dialog.show()
        mService.getPostList().enqueue(object : Callback<MutableList<Post>> {
            override fun onFailure(call: Call<MutableList<Post>>, t: Throwable) {
                Log.e("retrofit-module", "posts bad request: $t")
            }

            override fun onResponse(
                call: Call<MutableList<Post>>,
                response: Response<MutableList<Post>>
            ) {
                adapter = PostListAdapter(baseContext, response.body() as MutableList<Post>)
                adapter.setHasStableIds(true)
                adapter.notifyDataSetChanged()

                main_rv_posts_content.adapter = adapter

                dialog.dismiss()
            }
        })
    }
}