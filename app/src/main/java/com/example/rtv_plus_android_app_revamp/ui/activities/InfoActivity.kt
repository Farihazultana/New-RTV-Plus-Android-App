package com.example.rtv_plus_android_app_revamp.ui.activities

import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.rtv_plus_android_app_revamp.data.models.info.InfoResponse
import com.example.rtv_plus_android_app_revamp.databinding.ActivityInfoBinding
import com.example.rtv_plus_android_app_revamp.ui.viewmodels.InfoViewModel
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInfoBinding
    private val infoViewModel by viewModels<InfoViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(binding.root)

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "Info"
        }

        val intent = intent
        val appInfo = intent.getStringExtra("appinfo")

        infoViewModel.fetchInfo("8801825414747", appInfo.toString())

        infoViewModel.info.observe(this) { result ->
            when (result) {
                is ResultType.Loading -> {
                    binding.progressbar.visibility = View.VISIBLE
                }

                is ResultType.Success<*> -> {
                    val content = result.data as InfoResponse
                    if (content.details.isNotEmpty()) {

                        binding.infoDataTv.text = content.details
                        binding.infoDataTv.text = Html.fromHtml(content.details)
                        binding.infoDataTv.movementMethod = LinkMovementMethod.getInstance()



                        binding.progressbar.visibility = View.GONE
                    } else {
                        binding.progressbar.visibility = View.GONE
                        Toast.makeText(
                            this@InfoActivity,
                            "Something is wrong. Please try again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                is ResultType.Error -> {
                    Toast.makeText(
                        this@InfoActivity,
                        "Something is wrong. Please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }


    }
}