package com.devative.littledoor.util

/**
 * Created by AQUIB RASHID SHAIKH on 11-11-2023.
 */
import android.view.View
import androidx.viewpager2.widget.ViewPager2

class FadePageTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.apply {
            alpha = 0f
            visibility = View.VISIBLE

            // Counteract the default slide transition
            translationX = width * -position

            // Apply fade-in/fade-out effect
            animate()
                .alpha(if (position <= -1f || position >= 1f) 0f else 1f)
                .setDuration(500)
                .start()
        }
    }
}
