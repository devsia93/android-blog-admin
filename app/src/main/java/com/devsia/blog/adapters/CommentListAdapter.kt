package com.devsia.blog.adapters

import android.app.Dialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.devsia.blog.R
import com.devsia.blog.`interface`.RetrofitServices
import com.devsia.blog.common.Common
import com.devsia.blog.helper.Helper
import com.devsia.blog.models.Comment
import com.devsia.blog.preference.PreferenceHelper
import kotlinx.android.synthetic.main.item_comment.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CommentListAdapter(
    private val commentsList: MutableList<Comment>
) : RecyclerView.Adapter<CommentListAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var mService: RetrofitServices
        private val pref = PreferenceHelper(itemView.context)

        val tvNick: TextView = itemView.comment_nickname
        val tvText: TextView = itemView.comment_tv_text
        val tvDate: TextView = itemView.comment_date
        val ivHasApproved: ImageView = itemView.comment_iv_has_approved

        fun bind(listItem: Comment) {
            itemView.setOnLongClickListener {

                if (pref.getToken() == null)
                    showDialog()

                if (pref.getToken() != null) {
                    mService = Common.retrofitServices
                    mService.patchCommentById(
                        listItem.id,
                        pref.getToken()!!,
                        Helper.createJsonRequestBody("approved_comment" to (!listItem.approved_comment).toString())
                    )?.enqueue(object : Callback<Comment> {
                        override fun onResponse(call: Call<Comment>, response: Response<Comment>) {
                            ivHasApproved.setImageResource(if ((response.body() as Comment).approved_comment) R.drawable.ic_baseline_check_24 else R.drawable.ic_baseline_clear_24)
                        }

                        override fun onFailure(call: Call<Comment>, t: Throwable) {
                            Log.e("retrofit-module", "attempt to approve has been crashed: $t")
                        }
                    })
                }

                return@setOnLongClickListener true
            }
        }

        private fun showDialog() {
            val dialog = Dialog(itemView.context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_input_token)
            val etToken = dialog.findViewById<EditText>(R.id.input_token_et_token)
            val btnSave = dialog.findViewById(R.id.input_token_btn_save) as Button
            val btnCancel = dialog.findViewById(R.id.input_token_btn_cancel) as Button
            btnSave.setOnClickListener {
                pref.setToken(etToken.text.toString())
                dialog.dismiss()
            }
            btnCancel.setOnClickListener { dialog.dismiss() }
            dialog.show()
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