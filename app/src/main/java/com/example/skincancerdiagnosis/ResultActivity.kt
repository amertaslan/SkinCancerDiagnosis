package com.example.skincancerdiagnosis

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.skincancerdiagnosis.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_result)

        val extras = intent.extras
        val byteArray = extras!!.getByteArray("imageToShow")

        bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)

        binding.resultImage.setImageBitmap(bitmap)
    }
}