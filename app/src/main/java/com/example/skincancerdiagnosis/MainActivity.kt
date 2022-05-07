package com.example.skincancerdiagnosis

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import com.example.skincancerdiagnosis.databinding.ActivityMainBinding
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.File
import java.util.*


class MainActivity : AppCompatActivity() {

    companion object {
        const val GET_FROM_CAMERA = 10
        const val GET_FROM_GALLERY = 20
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        initializeListeners()
        enableRuntimePermission()
    }

    private fun initializeListeners() {
        binding.openCameraCard.setOnClickListener {
            val cameraImgIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraImgIntent.putExtra(
                MediaStore.EXTRA_OUTPUT,
                FileProvider.getUriForFile(
                    Objects.requireNonNull(applicationContext),
                    BuildConfig.APPLICATION_ID + ".provider",
                    File(getExternalFilesDir(null), "camera_image.jpg")
                )
            )
            startActivityForResult(cameraImgIntent, GET_FROM_CAMERA)
        }

        binding.openGalleryCard.setOnClickListener {
            val galleryImgIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryImgIntent.type = "image/*"
            val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
            galleryImgIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            galleryImgIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            startActivityForResult(galleryImgIntent, GET_FROM_GALLERY)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GET_FROM_CAMERA -> {
                if (resultCode == Activity.RESULT_OK) {
                    val imageUri = Uri.fromFile(File(getExternalFilesDir(null), "camera_image.jpg"))
                    CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .start(this)
                }
            }
            GET_FROM_GALLERY -> {
                if (resultCode == RESULT_OK) {
                    data?.data?.let { uri ->
                        CropImage.activity(uri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .setCropShape(CropImageView.CropShape.RECTANGLE)
                            .start(this)
                    }
                }
            }
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    result.uri?.let { uri ->
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.setClass(this, ResultActivity::class.java)
                        intent.putExtra("image_uri", uri)
                        startActivity(intent)

                        /*val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                        val resultIntent = Intent(this, ResultActivity::class.java)
                        val bs = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bs)
                        resultIntent.putExtra("byteArray", bs.toByteArray())
                        startActivity(resultIntent)*/
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            GET_FROM_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun enableRuntimePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            Toast.makeText(
                this,
                "Camera permission needed. Please allow in App Settings for additional functionality.",
                Toast.LENGTH_LONG
            ).show()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), GET_FROM_CAMERA)
        }
    }
}