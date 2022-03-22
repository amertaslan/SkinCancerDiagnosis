package com.example.skincancerdiagnosis

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.skincancerdiagnosis.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.openCameraCard.setOnClickListener {
            Toast.makeText(this, "Olduuuug", Toast.LENGTH_LONG).show()
        }
    }
}