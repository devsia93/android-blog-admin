package com.devsia.blog.activities

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devsia.blog.R
import com.devsia.blog.`interface`.RetrofitServices
import com.devsia.blog.adapters.PostListAdapter
import com.devsia.blog.common.Common
import com.devsia.blog.models.Post
import dmax.dialog.SpotsDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    lateinit var mService: RetrofitServices
    lateinit var layoutManager: LinearLayoutManager
    lateinit var adapter: PostListAdapter
    lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mService = Common.retrofitServices
        val recyclerPostList: RecyclerView = findViewById(R.id.main_rv_posts_content)
        recyclerPostList.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        recyclerPostList.layoutManager = layoutManager
        dialog = SpotsDialog.Builder().setCancelable(true).setContext(this).setTheme(R.style.Custom).build()

        getAllPostsList()
    }

    private fun getAllPostsList() {
        dialog.show()
        mService.getPostList().enqueue(object : Callback<MutableList<Post>> {
            override fun onFailure(call: Call<MutableList<Post>>, t: Throwable) {
                Log.e("retrofit", "failure ${t.toString()}")
            }

            override fun onResponse(
                call: Call<MutableList<Post>>,
                response: Response<MutableList<Post>>
            ) {
                adapter = PostListAdapter(baseContext, response.body() as MutableList<Post>)
                adapter.notifyDataSetChanged()
                val recyclerPostList: RecyclerView = findViewById(R.id.main_rv_posts_content)

                recyclerPostList.adapter = adapter

                dialog.dismiss()
            }
        })
    }
}