package com.devative.littledoor.ChatUi.liveStreaming

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.devative.littledoor.databinding.ActivityDigiLiveStartPointBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import es.dmoral.toasty.Toasty


class DigiLiveStartPoint : AppCompatActivity() {
    private val TAG: String = DigiLiveStartPoint::class.java.simpleName
    private lateinit var binding: ActivityDigiLiveStartPointBinding
    private var tips: AppCompatTextView? = null
    private val exampleBean: ExampleBean =
        ExampleBean(0, "ADVANCE", 2131820703, 2131296389, 2131820749)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDigiLiveStartPointBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val actionBar =  supportActionBar
        if (actionBar != null) {
            actionBar.setTitle(exampleBean.name)
            Log.d(
               TAG,
                "onViewCreated: getName " + exampleBean.name
            )
            actionBar.setHomeButtonEnabled(true)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        binding.next.setOnClickListener {
            if (binding.edtChannel.text.isEmpty()){
                Toasty.error(applicationContext,"Please enter channel id").show()
            }else{
                displayPopup()
            }
        }
        binding.topBack.setOnClickListener { finish() }

        runOnPermissionGranted()

    }

    private fun displayPopup() {
        val items = arrayOf("Broadcaster", "Audience")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Please Select the Role")
        builder.setItems(items) { dialog: DialogInterface, which: Int ->
            startActivity(Intent(applicationContext, LiveStreaming::class.java)
                .putExtra("CHANNEL_ID",binding.edtChannel.text.toString().trim())
                .putExtra("IS_HOST",which==0))
            dialog.dismiss()
        }

        builder.show()
    }

    private fun runOnPermissionGranted() {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA,
                Manifest.permission.BLUETOOTH_CONNECT
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    // Check if all permissions are granted
                    if (report?.areAllPermissionsGranted() == true) {

                    }else{
                        Toasty.error(applicationContext,"All permission required")
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    p1?.continuePermissionRequest()
                }

            })
            .check()
    }
}