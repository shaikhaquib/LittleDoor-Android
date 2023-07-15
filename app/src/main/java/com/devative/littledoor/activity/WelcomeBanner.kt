package com.devative.littledoor.activity

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.transition.Fade
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.devative.littledoor.R
import com.devative.littledoor.architecturalComponents.helper.Constants
import com.devative.littledoor.util.ZoomOutPageTransformer
import com.devative.littledoor.databinding.ActivityWelcomeBannerBinding
import com.devative.littledoor.databinding.IntroAppDesignBinding
import com.devative.littledoor.util.Utility


class WelcomeBanner : BaseActivity() {
    lateinit var binding: ActivityWelcomeBannerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fade = Fade()
        val decor = window.decorView
        fade.excludeTarget(android.R.id.statusBarBackground, true)
        fade.excludeTarget(android.R.id.navigationBarBackground, true)
        window.enterTransition = fade
        window.exitTransition = fade

        binding.viewPager2.adapter = AppIntroViewPager2Adapter()
        binding.viewPager2.setPageTransformer(ZoomOutPageTransformer())
        binding.tabLayout.attachTo(binding.viewPager2)

        binding.btnSkip.setOnClickListener {
            binding.viewPager2.setCurrentItem(MAX_STEP, true)
        }
        binding.btnNext.setOnClickListener {
            binding.viewPager2.setCurrentItem(binding.viewPager2.currentItem+1, true)
        }
        binding.btnGetStarted.setOnClickListener {
            Utility.savePrefBoolean(applicationContext,Constants.WELCOME_BANNER_SHOWN,true)
            startActivity(Intent(applicationContext, SignUpActivity::class.java))
        }

        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == MAX_STEP - 1) {
                    binding.linearLayout.visibility = View.GONE
                    binding.lilBtn.visibility = View.VISIBLE
                } else {
                    binding.linearLayout.visibility = View.VISIBLE
                    binding.lilBtn.visibility = View.GONE
                }
            }
        })

        checkNotificationPermission()

    }

    companion object {
        const val MAX_STEP = 2
    }


    class AppIntroViewPager2Adapter : RecyclerView.Adapter<PagerVH2>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerVH2 {
            return PagerVH2(
                IntroAppDesignBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }

        //get the size of color array
        override fun getItemCount(): Int = MAX_STEP // Int.MAX_VALUE

        //binding the screen with view
        override fun onBindViewHolder(holder: PagerVH2, position: Int) = holder.itemView.run {

            with(holder) {
                if (position == 0) {
                    bindingDesign.introTitle.text = "Lorem Ipsum is simply dummy text?"
                    bindingDesign.introDescription.text =
                        "Welcome !!! Do you want to clear task super fast with Mane?"
                    bindingDesign.introImage.setImageResource(R.drawable.splash_1)
                }
                if (position == 1) {
                    bindingDesign.introTitle.text = "Lorem Ipsum is simply dummy text?"
                    bindingDesign.introDescription.text =
                        "It has been easier to complete tasks. Get started with us!"
                    bindingDesign.introImage.setImageResource(R.drawable.splash_2)
                }
            }
        }
    }

    class PagerVH2(val bindingDesign: IntroAppDesignBinding) :
        RecyclerView.ViewHolder(bindingDesign.root)


    private fun checkNotificationPermission() {
        try {
            if (Build.VERSION.SDK_INT > 32) {
                ActivityCompat.requestPermissions(
                    this, arrayOf<String>(Manifest.permission.POST_NOTIFICATIONS),
                    1111
                )
            }
        } catch (_: Exception) {
        }
    }
}