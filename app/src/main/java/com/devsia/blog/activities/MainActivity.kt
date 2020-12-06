package com.devsia.blog.activities

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devsia.blog.R
import com.devsia.blog.`interface`.RetrofitServices
import com.devsia.blog.adapters.PostListAdapter
import com.devsia.blog.common.Common
import com.devsia.blog.models.Post
import com.devsia.blog.models.Tag
import com.devsia.blog.preference.Const
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

        initViews()

        getAllTagsList()
        getAllPostsList()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_toolbar_menu, menu)

        val search = menu!!.findItem(R.id.action_search)
        val searchView = search?.actionView as SearchView?
        searchView?.queryHint = resources.getString(R.string.search)
        val v: View? = searchView?.findViewById(R.id.search_plate)
        v?.setBackgroundColor(Color.TRANSPARENT)
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
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

        val tag = intent.extras?.get(Const.Extra.extraTag()) as Tag?

        if (tag != null) {
            mService.getPostsByIdTag(tag.id).enqueue(object : Callback<MutableList<Post>> {
                override fun onResponse(
                    call: Call<MutableList<Post>>,
                    response: Response<MutableList<Post>>
                ) {
                    inflateAdapter(response)
                }

                override fun onFailure(call: Call<MutableList<Post>>, t: Throwable) {
                    Log.e("retrofit-module", "posts by tag bad request: $t")
                }
            })
        } else {

            mService.getPostList().enqueue(object : Callback<MutableList<Post>> {
                override fun onFailure(call: Call<MutableList<Post>>, t: Throwable) {
                    Log.e("retrofit-module", "posts bad request: $t")
                }

                override fun onResponse(
                    call: Call<MutableList<Post>>,
                    response: Response<MutableList<Post>>
                ) {
                    inflateAdapter(response)
                }
            })
        }
    }

    private fun inflateAdapter(response: Response<MutableList<Post>>) {
        adapter = PostListAdapter(baseContext, response.body() as MutableList<Post>)
        adapter.setHasStableIds(true)
        adapter.notifyDataSetChanged()

        main_rv_posts_content.adapter = adapter

        dialog.dismiss()
    }

    private fun initViews() {

        main_toolbar.title = "Developer Blog"
        main_toolbar.subtitle = "Ivan Serov"
        setSupportActionBar(main_toolbar)
        main_toolbar.showOverflowMenu()

        main_rv_posts_content.setHasFixedSize(true)

        val tag: Tag? = intent.extras?.get(Const.Extra.extraTag()) as Tag?

        if (tag == null) {
            main_rv_posts_content.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy < 0 && !main_fb_add.isShown) main_fb_add.show() else if (dy > 0 && main_fb_add.isShown) main_fb_add.hide()
                }
            })
        } else main_fb_add.isVisible = false

        layoutManager = LinearLayoutManager(this)
        main_rv_posts_content.layoutManager = layoutManager
        dialog = SpotsDialog.Builder().setCancelable(true).setContext(this).setTheme(R.style.Custom)
            .build()
    }

    fun floatButtonOnClick(view: View){
        val intent = Intent(view.context, EditPostActivity::class.java)
        startActivity(intent)
    }

}