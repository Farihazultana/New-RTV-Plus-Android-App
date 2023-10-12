package com.rtvplus.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.rtvplus.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var calendar: Calendar
    private lateinit var footer: TextView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashDelay: Long = 2000
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        calendar = Calendar.getInstance()
        val year: Int = calendar.get(Calendar.YEAR)

        footer = findViewById(R.id.footer)
        footer.text = "©RTV & EBS $year. All Rights Reserved"

        CoroutineScope(Dispatchers.Main).launch {
            delay(splashDelay)

            val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}