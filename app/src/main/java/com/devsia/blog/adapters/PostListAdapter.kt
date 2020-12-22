package com.devsia.blog.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.devsia.blog.R
import com.devsia.blog.activities.MainActivity
import com.devsia.blog.activities.PostActivity
import com.devsia.blog.helper.Helper
import com.devsia.blog.models.Post
import com.devsia.blog.models.Tag
import com.devsia.blog.preference.Const
import com.devsia.blog.preference.PreferenceHelper
import com.google.android.flexbox.FlexboxLayout
import java.io.Serializable
import java.util.*


class PostListAdapter(
    private val context: Context,
    private val postListMain: List<Post>
) : RecyclerView.Adapter<PostListAdapter.ViewHolder>() {

    private var postList = mutableListOf<Post>()

    init {
        postList.addAll(postListMain)
    }

    fun filter(textSearch: String?) {
        val text = textSearch?.toLowerCase(Locale.ROOT)
        if (text.isNullOrEmpty()) {
            postList.clear()
            postList.addAll(postListMain)
        } else {
            val result = mutableListOf<Post>()
            for (post in postListMain) {
                if (post.body.toLowerCase(Locale.ROOT).contains(text) || post.title.toLowerCase(
                        Locale.ROOT
                    ).contains(text)
                ) {
                    result.add(post)
                }
            }
            postList.clear()
            postList.addAll(result)
        }
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.post_content_tv_title)
        val tvBody: TextView = itemView.findViewById(R.id.post_content_tv_body)
        val tvCommentsCount: TextView = itemView.findViewById(R.id.post_card_tv_comments_count)
        val tagMainLayout = itemView.findViewById<LinearLayout>(R.id.tags_content_main_layout)!!
        val fbView = itemView.findViewById<FlexboxLayout>(R.id.tags_content_fb_view)!!
        val ivTitle = itemView.findViewById<ImageView>(R.id.item_post_iv_title)!!


        fun bind(listItem: Post) {
            itemView.setOnClickListener {
                val intent = Intent(it.context, PostActivity::class.java)
                intent.putExtra(Const.Extra.extraPost(), listItem as Serializable)
                it.context.startActivity(intent)
            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_post,
            parent,
            false
        )

        return ViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listItem = postList[position]
        holder.bind(listItem)

        if (!listItem.image.isNullOrEmpty()) {
            holder.ivTitle.isVisible = true
            Glide
                .with(holder.itemView.context)
                .load(listItem.image)
                .centerCrop()
                .placeholder(ColorDrawable(Color.GRAY))
                .into(holder.ivTitle)
        } else holder.ivTitle.visibility = View.GONE

        holder.tvTitle.text = listItem.title
        holder.tvBody.text = if (listItem.body.length > Const.Setting.countCharsForPost())
            "${listItem.body.take(Const.Setting.countCharsForPost()).substringBeforeLast(".")}..."
        else listItem.body

        val isTagged = listItem.tags?.isNotEmpty() ?: false
        holder.tagMainLayout.isVisible = isTagged

        if (isTagged) {
            val tags: List<Tag> = PreferenceHelper.getListTags(context)

            for (tagId in listItem.tags!!) {
                for (tag in tags) {
                    if (tag.id == tagId) {
                        val chip = Helper.getCreatedChip(holder.fbView, tag)

                        if (chip != null) {
                            holder.fbView.addView(chip)

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

        if (listItem.comment_count > 0)
            holder.tvCommentsCount.isVisible = true
        holder.tvCommentsCount.text = "${context.getString(R.string.comments)} " +
                listItem.comment_count
    }

    fun updatePostsList(list: MutableList<Post>) {
        postList.clear()
        postList = list
        this.notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int) = position

    override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(true)
    }

    override fun getItemCount() = postList.size

    override fun getItemId(position: Int) = position.toLong()
}