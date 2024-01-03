package com.devative.littledoor.util

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

class ZoomOutPageTransformer : ViewPager2.PageTransformer {

    override fun transformPage(view: View, position: Float) {
        view.translationX = view.width * -position
        if(position <= -1.0F || position >= 1.0F) {
            view.alpha = 0.0F
        } else if( position == 0.0F ) {
            view.alpha = 1.0F
        } else {
            // position is between -1.0F & 0.0F OR 0.0F & 1.0F
            view.alpha = 1.0F - abs(position)
        }
        view.animate().duration =
            view.resources.getInteger(android.R.integer.config_longAnimTime).toLong()
    }

}
