package com.devsia.blog.adapters

import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.devsia.blog.*
import com.devsia.blog.`interface`.RetrofitServices
import com.devsia.blog.helper.Helper
import com.devsia.blog.models.Comment
import kotlinx.android.synthetic.main.item_comment.view.*


class CommentListAdapter(
    private val commentsList: MutableList<Comment>
) : RecyclerView.Adapter<CommentListAdapter.ViewHolder>() {
    lateinit var selectedItems: SparseBooleanArray

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var mService: RetrofitServices

        val tvNick: TextView = itemView.comment_nickname
        val tvText: TextView = itemView.comment_tv_text
        val tvDate: TextView = itemView.comment_date
        val ivHasApproved: ImageView = itemView.comment_iv_has_approved
        val cvComment: CardView = itemView.comment_cv_main
        var isSelected: Boolean = false

        fun bind(listItem: Comment) {
            itemView.setOnLongClickListener {
                Helper.checkAvailableToken(it.context)

//                if (PreferenceHelper.getToken(it.context) != null) {
//                    mService = Common.retrofitServices
//                    mService.patchCommentById(
//                        listItem.id,
//                        PreferenceHelper.getToken(it.context)!!,
//                        Helper.createJsonRequestBody("approved_comment" to (!listItem.approved_comment).toString())
//                    )?.enqueue(object : Callback<Comment> {
//                        override fun onResponse(call: Call<Comment>, response: Response<Comment>) {
//                            ivHasApproved.setImageResource(if ((response.body() as Comment).approved_comment) R.drawable.ic_baseline_check_24 else R.drawable.ic_baseline_clear_24)
//                        }
//
//                        override fun onFailure(call: Call<Comment>, t: Throwable) {
//                            Log.e("retrofit-module", "attempt to approve has been crashed: $t")
//                        }
//                    })
//                }


                return@setOnLongClickListener true
            }
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
        holder.bind(listItem)

        holder.tvNick.text = listItem.author_name
        holder.tvDate.text = listItem.date_pub.substringBefore('T')
        holder.tvText.text = listItem.text
        holder.ivHasApproved.setImageResource(if (listItem.approved_comment) R.drawable.ic_baseline_check_24 else R.drawable.ic_baseline_clear_24)
    }

    override fun getItemCount(): Int = commentsList.size

}