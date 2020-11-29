package com.devsia.blog.activities

import android.app.AlertDialog
import android.os.Bundle
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

class ActivityPost : AppCompatActivity() {

    lateinit var mService: RetrofitServices
    lateinit var layoutManager: LinearLayoutManager
    lateinit var adapter: PostListAdapter
    lateinit var dialog: AlertDialog
    var recyclerPostList: RecyclerView = findViewById(R.id.main_rv_posts_content)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        mService = Common.retrofitServices
//        var recyclerPostList: RecyclerView = findViewById(R.id.main_rv_posts_content)
        recyclerPostList.layoutManager = layoutManager
        dialog = SpotsDialog.Builder().setCancelable(true).setContext(this).build()

        getAllPostsList()
    }

    private fun getAllPostsList() {
        dialog.show()
        mService.getPostList().enqueue(object : Callback<MutableList<Post>>{
            override fun onFailure(call: Call<MutableList<Post>>, t: Throwable) {
                TODO("Not yet implemented")
            }

            override fun onResponse(
                call: Call<MutableList<Post>>,
                response: Response<MutableList<Post>>
            ) {
                adapter = PostListAdapter(baseContext, response.body() as MutableList<Post>)
                adapter.notifyDataSetChanged()
                recyclerPostList.adapter = adapter

                dialog.dismiss()
            }
        })
    }
}