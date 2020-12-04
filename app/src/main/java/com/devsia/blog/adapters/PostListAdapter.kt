package com.devsia.blog.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.devsia.blog.R
import com.devsia.blog.activities.ActivityPost
import com.devsia.blog.helper.Helper
import com.devsia.blog.models.Post
import com.devsia.blog.models.Tag
import com.devsia.blog.preference.Const
import com.devsia.blog.preference.PreferenceHelper
import com.google.android.flexbox.FlexboxLayout
import java.io.Serializable


class PostListAdapter(
    private val context: Context,
    private val postList: MutableList<Post>
) : RecyclerView.Adapter<PostListAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.post_content_tv_title)
        val tvBody: TextView = itemView.findViewById(R.id.post_content_tv_body)
        val tvCommentsCount: TextView = itemView.findViewById(R.id.post_card_tv_comments_count)
        val tagMainLayout = itemView.findViewById<LinearLayout>(R.id.tags_content_main_layout)!!
        val fbView = itemView.findViewById<FlexboxLayout>(R.id.tags_content_fb_view)!!


        fun bind(listItem: Post) {
            itemView.setOnClickListener {
                val intent = Intent(it.context, ActivityPost::class.java)
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

        holder.tvTitle.text = listItem.title
        holder.tvBody.text = if (listItem.body.length > Const.Setting.countCharsForPost())
            "${listItem.body.take(Const.Setting.countCharsForPost()).substringBeforeLast(".")}..."
        else listItem.body

        val isTagged = listItem.tags?.isNotEmpty() ?: false
        holder.tagMainLayout.isVisible = isTagged

        if (isTagged) {
            val pref = PreferenceHelper(context)
            val tags: List<Tag> = pref.getListTags()
            for (tagId in listItem.tags!!) {
                for (tag in tags) {
                    if (tag.id == tagId)
                        Helper.createChip(holder.fbView, tag.title)
                }
            }
        }

        if (!listItem.comments.isNullOrEmpty())
            holder.tvCommentsCount.isVisible = true
        holder.tvCommentsCount.text = "${context.getString(R.string.comments)} " +
                "${listItem.comments!!.size}"
    }

    override fun getItemViewType(position: Int) = position

    override fun getItemCount() = postList.size

    override fun getItemId(position: Int) = position.toLong()

}