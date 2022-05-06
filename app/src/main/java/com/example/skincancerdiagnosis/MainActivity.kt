package com.example.skincancerdiagnosis

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*


class MainActivity : AppCompatActivity() {

    companion object {
        const val GET_FROM_CAMERA = 10
        const val GET_FROM_GALLERY = 20
    }

    private lateinit var thumbnail: Bitmap
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
            startActivityForResult(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                GET_FROM_GALLERY -> { getCroppedImage(data) }
                GET_FROM_CAMERA -> { getCroppedImage(data?.extras?.getParcelable(MediaStore.EXTRA_OUTPUT)) }
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> { runOnCropImage(data) }
            }
        }
    }

    private fun runOnCropImage(data: Intent?) {
        try {
            thumbnail = MediaStore.Images.Media.getBitmap(this.contentResolver, data?.data!!)
            binding.cameraIconImageview.setImageBitmap(thumbnail)
            //goToResultActivity(createImageByteArray(thumbnail))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getCroppedImage(data: Intent?) {
        try {
            data?.data?.let {
                launchImageCrop(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun launchImageCrop(imageUri: Uri) {
        CropImage.activity(imageUri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .start(this);
    }

    /*

    private fun runOnGalleryUpload(data: Intent?) {
        val selectedImage: Uri = data?.data!!
        try {
            thumbnail = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
            goToResultActivity(createImageByteArray(thumbnail, IMAGE_SIZE_TO_TEST), createImageByteArray(thumbnail, IMAGE_SIZE_TO_SHOW))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun runOnCameraUpload(data: Intent?) {
        try {
            thumbnail = data?.extras?.get("data") as Bitmap
            goToResultActivity(createImageByteArray(thumbnail, IMAGE_SIZE_TO_TEST), createImageByteArray(thumbnail, IMAGE_SIZE_TO_SHOW))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    */

    private fun resizeImage(image: Bitmap, imageSize: Int): Bitmap = Bitmap.createScaledBitmap(image, imageSize, imageSize, false)

    private fun createImageByteArray(image: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
        /*
        val imageToByteArray = resizeImage(image, imageSize)
        val stream = ByteArrayOutputStream()
        imageToByteArray.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
        */
    }

    private fun goToResultActivity(imageToTest: ByteArray) {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("imageToTest", imageToTest)
        startActivity(intent)
        /*
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("imageToTest", imageToTest)
        intent.putExtra("imageToShow", imageToShow)
        startActivity(intent)
        */
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