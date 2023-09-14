package com.example.rtv_plus_android_app_revamp.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.rtv_plus_android_app_revamp.R
import java.util.logging.Handler

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val SPLASH_DELAY: Long = 2000
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        android.os.Handler().postDelayed({
            val intent = Intent(this@SplashScreenActivity, MainActivity::class.java) // Replace with your main activity class
            startActivity(intent)
            finish()
        }, SPLASH_DELAY)
    }
}