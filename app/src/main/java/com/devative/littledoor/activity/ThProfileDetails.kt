package com.devative.littledoor.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.devative.littledoor.R
import com.devative.littledoor.databinding.ActivityThProfileDetailsBinding

class ThProfileDetails : BaseActivity() {
    val binding: ActivityThProfileDetailsBinding by lazy {
        ActivityThProfileDetailsBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.btnBookAppointment.setOnClickListener {
            startActivity(Intent(applicationContext,BookAppointment::class.java))
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}