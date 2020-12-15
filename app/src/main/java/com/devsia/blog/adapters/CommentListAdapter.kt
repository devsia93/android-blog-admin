package com.devsia.blog.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.devsia.blog.*
import com.devsia.blog.models.CommentService
import kotlinx.android.synthetic.main.item_comment.view.*


class CommentListAdapter(
    private val commentsList: MutableList<CommentService>
) : RecyclerView.Adapter<CommentListAdapter.ViewHolder>() {

    var selectedCommentService = mutableMapOf<CommentService, Boolean>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvNick: TextView = itemView.comment_nickname
        val tvText: TextView = itemView.comment_tv_text
        val tvDate: TextView = itemView.comment_date
        val ivHasApproved: ImageView = itemView.comment_iv_has_approved

        fun bind(
            listItem: CommentService,
            position: Int,
            selectedCommentService: MutableMap<CommentService, Boolean>
        ) {
            itemView.setOnLongClickListener {
                commonClick(it, listItem, selectedCommentService)
                return@setOnLongClickListener true
            }

            itemView.setOnClickListener {
                if (selectedCommentService.isNotEmpty()) {
                    commonClick(it, listItem, selectedCommentService)
                }
            }
        }

        private fun commonClick(
            it: View,
            listItem: CommentService,
            selectedCommentService: MutableMap<CommentService, Boolean>
        ) {
            it.isSelected = !it.isSelected
            it.isActivated = !it.isActivated
            listItem.setSelected(!listItem.getSelected())
            if (selectedCommentService[listItem] != null || selectedCommentService[listItem] == true) {
                selectedCommentService.remove(listItem)
            } else selectedCommentService[listItem] = true
        }
    }

    override fun getItemViewType(position: Int): Int = position

    override fun getItemId(position: Int): Long = position.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listItem = commentsList[position]
        holder.bind(listItem, position, selectedCommentService)

        holder.tvNick.text = listItem.author_name
        holder.tvDate.text = listItem.date_pub.substringBefore('T')
        holder.tvText.text = listItem.text
        holder.ivHasApproved.setImageResource(if (listItem.approved_comment) R.drawable.ic_baseline_visibility_24 else R.drawable.ic_baseline_visibility_off_24)

        if (selectedCommentService.containsKey(listItem)) {
            holder.itemView.isSelected = true
            holder.itemView.isActivated = true
        } else {
            holder.itemView.isSelected = false
            holder.itemView.isActivated = false
        }
    }

    override fun getItemCount(): Int = commentsList.size

    fun clearSelectedItems(){
        selectedCommentService.clear()
    }

}