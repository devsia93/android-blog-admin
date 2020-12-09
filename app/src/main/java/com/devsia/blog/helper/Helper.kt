package com.devsia.blog.helper

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import androidx.core.view.children
import androidx.core.view.marginStart
import com.devsia.blog.R
import com.devsia.blog.models.Tag
import com.devsia.blog.preference.PreferenceHelper
import com.google.android.flexbox.FlexboxLayout
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.chip.Chip
import okhttp3.RequestBody
import org.json.JSONObject

class Helper {
    companion object Builder {
        fun getCreatedChip(
            fbView: FlexboxLayout,
            tag: Tag,
            isCheckable: Boolean = false,
            isChecked: Boolean = false
        ): Chip? {
            //if chip already exist:
            for (view in fbView.children)
                if (view is Chip && view.text == tag.title)
                    return null

            val chip = Chip(fbView.context)
            chip.text = tag.title
            chip.isClickable = true
            chip.marginStart
            chip.layoutParams = getLayoutParams(chip.context)
            chip.isCheckable = isCheckable
            chip.isChecked = isChecked

            return chip
        }

        fun createJsonRequestBody(vararg params: Pair<String, Any>): RequestBody =
            RequestBody.create(
                okhttp3.MediaType.parse("application/json; charset=utf-8"),
                JSONObject(mapOf(*params)).toString()
            )

        fun getColorIdByAttr(attr: Int, theme: Resources.Theme): Int {
            val typedValue = TypedValue()
            theme.resolveAttribute(attr, typedValue, true)
            return typedValue.data
        }

        fun checkAvailableToken(context: Context) {
            if (PreferenceHelper.getToken(context).isNullOrEmpty())
                showDialogAndSetToken(context)
        }

        private fun showDialogAndSetToken(context: Context) {
            val customLayout: View = (context as Activity).layoutInflater
                .inflate(
                    R.layout.dialog_input_token,
                    null
                )
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                .setTitle("Token")
                .setMessage("Please, input your value of token:")
                .setView(customLayout)
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(
                    "Save"
                ) { _, _ ->
                    val editText = customLayout
                        .findViewById<EditText>(
                            R.id.input_token_et_token
                        )
                    setToken(
                        context,
                        editText
                            .text
                            .toString()
                    )
                }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        private fun setToken(context: Context, token: String) {
            PreferenceHelper.setToken(context, token)
        }

        private fun getLayoutParams(context: Context): FlexboxLayoutManager.LayoutParams {
            val marginInDp = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, "6".toFloat(), context.resources
                    .displayMetrics
            ).toInt()
            val params = FlexboxLayoutManager.LayoutParams(
                FlexboxLayoutManager.LayoutParams.WRAP_CONTENT,
                FlexboxLayoutManager.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(marginInDp, 0, 0, 0)
            return params
        }
    }
}