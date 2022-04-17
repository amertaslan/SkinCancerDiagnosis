package com.example.skincancerdiagnosis

import android.R.attr
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.skincancerdiagnosis.databinding.ActivityMainBinding
import java.io.FileNotFoundException
import java.io.IOException


class MainActivity : AppCompatActivity() {

    companion object {
        const val GET_FROM_CAMERA = 1
        const val GET_FROM_GALLERY = 2
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.openCameraCard.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, GET_FROM_CAMERA)
        }

        binding.openGalleryCard.setOnClickListener {
            startActivityForResult(
                Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.INTERNAL_CONTENT_URI
                ),
                GET_FROM_GALLERY
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                GET_FROM_GALLERY -> {
                    val selectedImage: Uri = data?.data!!
                    var bitmap: Bitmap? = null
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                        binding.galleryIconImageview.setImageBitmap(bitmap)
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                GET_FROM_CAMERA -> {
                    val thumbnail = data!!.extras!!.get("data") as Bitmap
                    binding.cameraIconImageview.setImageBitmap(thumbnail)
                }
            }
        }
    }
}