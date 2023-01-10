package com.armutyus.ninova.constants

import android.content.Context
import android.view.View
import android.view.animation.AlphaAnimation
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.armutyus.ninova.R

class Util {

    companion object {
        fun progressDrawable(context: Context): CircularProgressDrawable {
            val circularProgressDrawable = CircularProgressDrawable(context)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 30f
            circularProgressDrawable.backgroundColor = R.color.md_theme_light_tertiary
            circularProgressDrawable.start()

            return circularProgressDrawable
        }

        fun View.fadeIn(durationMillis: Long) {
            this.startAnimation(AlphaAnimation(0F, 1F).apply {
                duration = durationMillis
                fillAfter = false
            })
        }
    }

}