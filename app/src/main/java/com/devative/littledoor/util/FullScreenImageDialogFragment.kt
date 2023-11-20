package com.devative.littledoor.util

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.DialogFragment
import com.devative.littledoor.R
import com.squareup.picasso.Picasso

/**
 * Created by AQUIB RASHID SHAIKH on 03-11-2023.
 */
class FullScreenImageDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.dialog_fullscreen_image, null)

        val imageUri = arguments?.getString("imageUri")
        val imageView = view.findViewById<AppCompatImageView>(R.id.imageView)
        Picasso.get()
            .load(imageUri)
            .placeholder(R.color.grey_primary)
            .into(imageView)

        view.findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            dismiss()
        }

        val dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.setContentView(view)
        return dialog
    }
}
