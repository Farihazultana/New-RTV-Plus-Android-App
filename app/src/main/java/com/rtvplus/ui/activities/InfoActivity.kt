package com.rtvplus.ui.activities

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.rtvplus.R
import com.rtvplus.data.models.info.InfoResponse
import com.rtvplus.databinding.ActivityInfoBinding
import com.rtvplus.ui.viewmodels.InfoViewModel
import com.rtvplus.utils.AppUtils
import com.rtvplus.utils.ResultType
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class InfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInfoBinding
    private val infoViewModel by viewModels<InfoViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoBinding.inflate(layoutInflater)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(binding.root)
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        if (!AppUtils.isOnline(this)) {
            AppUtils.showAlertDialog(this)
        }

        val intent = intent
        val appInfo = intent.getStringExtra("appinfo")

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            if (appInfo != null) {
                title =
                    appInfo.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                titleColor = Color.BLACK
            }
        }

        infoViewModel.fetchInfo("", appInfo.toString())

        infoViewModel.info.observe(this) { result ->
            when (result) {
                is ResultType.Loading -> {
                    binding.progressbar.visibility = View.VISIBLE
                }

                is ResultType.Success<*> -> {
                    val content = result.data as InfoResponse
                    if (content.details.isNotEmpty()) {
                        //binding.infoDataWv.text = content.details
                        binding.infoDataWv.loadDataWithBaseURL(null, content.details, "text/html", "utf-8", null);
                        binding.infoDataWv.setOnLongClickListener { true }
                        binding.infoDataWv.isLongClickable = false
                        binding.progressbar.visibility = View.GONE
                    } else {
                        binding.progressbar.visibility = View.GONE
                        Toast.makeText(
                            this@InfoActivity,
                            R.string.error_response_msg,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                is ResultType.Error -> {
                    Toast.makeText(
                        this@InfoActivity,
                        R.string.error_response_msg,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}