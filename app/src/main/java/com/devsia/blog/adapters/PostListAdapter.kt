package com.devsia.blog.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.devsia.blog.R
import com.devsia.blog.models.Post

class PostListAdapter(
    private val context: Context,
    private val postList: MutableList<Post>
):RecyclerView.Adapter<PostListAdapter.ViewHolder>() {
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tv_title: TextView = itemView.findViewById(R.id.post_content_tv_title_view)
        val tv_body: TextView = itemView.findViewById(R.id.post_content_tv_body_view)
        val cv_post: CardView = itemView.findViewById(R.id.post_card_cv_post)
        val tag_main_layout = itemView.findViewById<LinearLayout>(R.id.tags_content_main_layout)


        fun bind(listItem: Post){
            cv_post.setOnClickListener{
                Toast.makeText(it.context, "press on $cv_post", Toast.LENGTH_SHORT).show()
            }
            itemView.setOnClickListener{
                Toast.makeText(it.context, "press on ${itemView.findViewById<TextView>
                    (R.id.post_content_tv_title_view).text}",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listItem = postList[position]
        holder.bind(listItem)

        holder.tv_title.text = listItem.title
        holder.tv_body.text = listItem.body

        //sorry, it is a temporarily
        if (listItem.tags != null && listItem.tags.isEmpty())
            holder.tag_main_layout.isVisible = true
        else
            holder.tag_main_layout.isVisible = false

    }

    override fun getItemCount() = postList.size


}