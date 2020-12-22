package com.devsia.blog.activities

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devsia.blog.R
import com.devsia.blog.`interface`.RetrofitServices
import com.devsia.blog.adapters.PostListAdapter
import com.devsia.blog.common.Common
import com.devsia.blog.helper.Helper
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
    private lateinit var searchView: SearchView
    private lateinit var adapter: PostListAdapter
    private lateinit var dialog: AlertDialog
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mService = Common.retrofitServices

        handler = Handler(Looper.getMainLooper())

        initViews()

        getAllTagsList()
        getAllPostsList()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_toolbar_menu, menu)

        val search = menu!!.findItem(R.id.action_search)
        searchView = search.actionView as SearchView
        searchView.queryHint = resources.getString(R.string.search)

        val searchFrameId =
            searchView.context.resources.getIdentifier("android:id/search_edit_frame", null, null)
        val searchFrame = searchView.findViewById<View>(searchFrameId)
        searchFrame.background = ContextCompat.getDrawable(this, R.drawable.bg_searchview)

        val searchPlateId =
            searchView.context.resources.getIdentifier("android:id/search_plate", null, null)
        val searchPlate = searchView.findViewById<View>(searchPlateId)
        searchPlate!!.setBackgroundColor(Color.TRANSPARENT)
        searchPlate.background = null

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapter.filter(query)
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter(newText)
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
                PreferenceHelper.saveListTags(baseContext, response.body() as List<Tag>)
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
            requestToGetPosts()
        }
    }

    private fun requestToGetPosts(isRefreshing: Boolean = false) {
        mService.getPostList().enqueue(object : Callback<MutableList<Post>> {
            override fun onFailure(call: Call<MutableList<Post>>, t: Throwable) {
                Log.e("retrofit-module", "posts bad request: $t")
            }

            override fun onResponse(
                call: Call<MutableList<Post>>,
                response: Response<MutableList<Post>>
            ) {
                inflateAdapter(response, isRefreshing)
            }
        })
    }

    private fun inflateAdapter(
        response: Response<MutableList<Post>>,
        isRefreshing: Boolean = false
    ) {
        if (isRefreshing) {
            adapter.updatePostsList(response.body() as MutableList<Post>)
        } else {
            adapter = PostListAdapter(baseContext, response.body() as MutableList<Post>)
            adapter.setHasStableIds(true)
            adapter.notifyDataSetChanged()

            main_rv_posts_content.adapter = adapter

            dialog.dismiss()
        }
    }

    private fun initViews() {
        main_toolbar.title = "Developer Blog"
        main_toolbar.subtitle = "Ivan Serov"
        setSupportActionBar(main_toolbar)
        main_toolbar.showOverflowMenu()

        main_rv_posts_content.setHasFixedSize(true)

        val tag: Tag? = intent.extras?.get(Const.Extra.extraTag()) as Tag?
        //if activity has no intent with tag then not showing fb
        if (tag == null) {
            main_rv_posts_content.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy < 0 && !main_fb_add.isShown) main_fb_add.show() else if (dy > 0 && main_fb_add.isShown) {
                        main_fb_add.hide()
                        searchView.clearFocus()
                    }
                }
            })
        } else main_fb_add.isVisible = false
        //swipe refresh
        main_swipe_refresh.setOnRefreshListener {
            runnable = Runnable {
                if (!searchView.isIconified) {
                    searchView.isIconified = true
                    searchView.clearFocus()
                }
                requestToGetPosts(isRefreshing = true)
                main_swipe_refresh.isRefreshing = false
            }

            handler.postDelayed(runnable, 2000)
        }
        main_swipe_refresh.setProgressBackgroundColorSchemeColor(
            Helper.getColorIdByAttr(
                R.attr.colorSecondary,
                theme
            )
        )
        main_swipe_refresh.setColorSchemeColors(
            Helper.getColorIdByAttr(
                R.attr.colorOnSecondary,
                theme
            )
        )
        //recycler view
        layoutManager = LinearLayoutManager(this)
        main_rv_posts_content.layoutManager = layoutManager
        dialog = SpotsDialog.Builder().setCancelable(true).setContext(this).setTheme(R.style.Custom)
            .build()
    }

    fun floatButtonOnClick(view: View) {
        val intent = Intent(view.context, EditPostActivity::class.java)
        startActivity(intent)
    }
}