package com.devsia.blog.helper

import android.content.Context
import android.util.TypedValue
import androidx.core.view.children
import androidx.core.view.marginStart
import com.devsia.blog.models.Tag
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

        fun createJsonRequestBody(vararg params: Pair<String, Any>) =
            RequestBody.create(
                okhttp3.MediaType.parse("application/json; charset=utf-8"),
                JSONObject(mapOf(*params)).toString()
            )

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