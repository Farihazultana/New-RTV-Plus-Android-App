package com.example.rtv_plus_android_app_revamp.ui.activities

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.example.rtv_plus_android_app_revamp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {

    lateinit var animationDrawable: AnimationDrawable
    override fun onCreate(savedInstanceState: Bundle?) {
        val SPLASH_DELAY: Long = 3000
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val loading = findViewById<ImageView>(R.id.loadingIndicator)
        animationDrawable = loading.drawable as AnimationDrawable
        animationDrawable.start()

        CoroutineScope(Dispatchers.Main).launch {
            delay(SPLASH_DELAY)

            val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
            startActivity(intent)


            finish()
        }

    }
}