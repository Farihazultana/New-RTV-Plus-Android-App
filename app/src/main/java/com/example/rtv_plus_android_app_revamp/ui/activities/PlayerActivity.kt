package com.example.rtv_plus_android_app_revamp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.rtv_plus_android_app_revamp.R
import com.example.rtv_plus_android_app_revamp.databinding.ActivityMainBinding
import com.example.rtv_plus_android_app_revamp.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val receivedValue = intent.getStringExtra("id")
        if (receivedValue != null) {
            val textView = binding.idTv
            textView.text = receivedValue
        }
    }
}