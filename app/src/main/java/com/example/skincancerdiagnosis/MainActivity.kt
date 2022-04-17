package com.example.skincancerdiagnosis

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.skincancerdiagnosis.databinding.ActivityMainBinding
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {

    companion object {
        const val GET_FROM_CAMERA = 1
        const val GET_FROM_GALLERY = 2
        const val IMAGE_SIZE = 224
    }

    private lateinit var thumbnail: Bitmap
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        initializeListeners()
    }

    private fun initializeListeners() {
        binding.openCameraCard.setOnClickListener {
            startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE), GET_FROM_CAMERA)
        }

        binding.openGalleryCard.setOnClickListener {
            startActivityForResult(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                GET_FROM_GALLERY -> { runOnGalleryUpload(data) }
                GET_FROM_CAMERA -> { runOnCameraUpload(data) }
            }
        }
    }

    private fun runOnGalleryUpload(data: Intent?) {
        val selectedImage: Uri = data?.data!!
        try {
            thumbnail = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
            val resizedImage = Bitmap.createScaledBitmap(thumbnail, IMAGE_SIZE, IMAGE_SIZE, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun runOnCameraUpload(data: Intent?) {
        try {
            thumbnail = data?.extras?.get("data") as Bitmap
            val resizedImage = Bitmap.createScaledBitmap(thumbnail, IMAGE_SIZE, IMAGE_SIZE, false)
            binding.cameraIconImageview.setImageBitmap(resizedImage)
            val stream = ByteArrayOutputStream()
            resizedImage.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray: ByteArray = stream.toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}