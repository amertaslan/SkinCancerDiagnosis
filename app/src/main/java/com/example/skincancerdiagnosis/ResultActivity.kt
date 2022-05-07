package com.example.skincancerdiagnosis

import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.skincancerdiagnosis.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_result)

        val extras = intent.extras
        val uri = extras?.getParcelable<Uri>("image_uri")

        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)

        binding.resultImage.setImageBitmap(bitmap)
    }
}