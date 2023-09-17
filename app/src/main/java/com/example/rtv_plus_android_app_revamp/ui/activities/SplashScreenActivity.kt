package com.example.rtv_plus_android_app_revamp.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.rtv_plus_android_app_revamp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

class SplashScreenActivity : AppCompatActivity() {

    lateinit var calendar: Calendar
    lateinit var footer: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        val SPLASH_DELAY: Long = 2000
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        calendar = Calendar.getInstance()
        var year : Int = calendar.get(Calendar.YEAR)

        footer = findViewById(R.id.footer)
        footer.text = "©RTV & EBS $year. All Rights Reserved"


        CoroutineScope(Dispatchers.Main).launch {
            delay(SPLASH_DELAY)

            val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
            startActivity(intent)


            finish()
        }

    }
}