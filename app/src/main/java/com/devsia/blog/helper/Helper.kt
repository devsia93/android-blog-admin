package com.devsia.blog.helper

import android.content.Context
import android.util.TypedValue
import androidx.core.view.children
import androidx.core.view.marginStart
import com.google.android.flexbox.FlexboxLayout
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.chip.Chip

class Helper {
    companion object Builder{
         fun createChip(fbView: FlexboxLayout, tagTitle: String) {
            //if chip already exist:
            for (view in fbView.children)
                if (view is Chip && view.text == tagTitle)
                    return

            val chip = Chip(fbView.context)
            chip.text = tagTitle
            chip.isClickable = true
            chip.marginStart
            chip.layoutParams = getLayoutParams(chip.context)

            fbView.addView(chip)
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