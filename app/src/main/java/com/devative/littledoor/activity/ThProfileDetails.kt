package com.devative.littledoor.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.content.ContextCompat
import com.devative.littledoor.R
import com.devative.littledoor.adapter.DoctorDetailsAdapter
import com.devative.littledoor.architecturalComponents.helper.Constants.TH_DETAILS
import com.devative.littledoor.architecturalComponents.helper.Constants.load
import com.devative.littledoor.databinding.ActivityThProfileDetailsBinding
import com.devative.littledoor.model.DoctotorListRes
import com.google.android.material.chip.Chip

class ThProfileDetails : BaseActivity() {
    val binding: ActivityThProfileDetailsBinding by lazy {
        ActivityThProfileDetailsBinding.inflate(layoutInflater)
    }
    val thDetails:DoctotorListRes.Data by lazy {
        intent.getSerializableExtra(TH_DETAILS) as DoctotorListRes.Data
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.btnBookAppointment.setOnClickListener {
            startActivity(Intent(applicationContext,BookAppointment::class.java)
                .putExtra(TH_DETAILS,thDetails))
        }
        setData()
    }

    private fun setData() {
        thDetails.apply {
            binding.apply {
                image?.let { imgProfile.load(it,R.drawable.sample_dr_image) }
                txtCategory.text = category_name
                txtCity.text = city
                txtYearEXP.text = "$total_year_of_experience"
                toolbar.title = name
                txtSessionFee.text = "$doctor_session_charge"


                rvData.adapter = DoctorDetailsAdapter(this@ThProfileDetails,thDetails,object :
                    DoctorDetailsAdapter.FormMasterEvent {
                    override fun onClick(position: Int) {
                    }

                    override fun onClickAdd(position: Int) {
                    }

                    override fun onEdit(type: Any, position: Int) {
                    }

                    override fun onExpertiseRemove(list: ArrayList<String>) {
                    }

                    override fun onLangRemove(list: ArrayList<String>) {
                    }
                })
            }
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