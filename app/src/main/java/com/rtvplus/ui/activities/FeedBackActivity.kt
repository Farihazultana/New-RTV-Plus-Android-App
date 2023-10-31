package com.rtvplus.ui.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.rtvplus.R
import com.rtvplus.data.models.comment.CommentResponse
import com.rtvplus.data.models.feedback.FeedbackResponse
import com.rtvplus.databinding.ActivityFeedBackBinding
import com.rtvplus.ui.viewmodels.LogInViewModel
import com.rtvplus.ui.viewmodels.SaveFeedbackViewModel
import com.rtvplus.utils.AppUtils
import com.rtvplus.utils.ResultType
import com.rtvplus.utils.SharedPreferencesUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedBackActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFeedBackBinding
    private val feedbackViewmodel by viewModels<SaveFeedbackViewModel>()
    lateinit var username: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!AppUtils.isOnline(this)) {
            AppUtils.showAlertDialog(this)
        }

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        username = SharedPreferencesUtil.getData(this, AppUtils.UsernameInputKey, "").toString()

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "FeedBack"
        }

        binding.contentEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(s: Editable?) {
                val currentCharacterCount = s?.length ?: 0
                binding.textCounter.text = "$currentCharacterCount of 500"
            }
        })

        binding.sentFeedbackBtn.setOnClickListener{
            val userInput = binding.contentEditText.text.toString()
            feedbackViewmodel.saveFeedback(username,userInput)
            checkResponse()
        }
    }

    private fun checkResponse() {
        feedbackViewmodel.saveFeedbackResponse.observe(this) { it ->
            when (it) {
                is ResultType.Loading -> {
                    binding.progressbar.visibility = View.VISIBLE
                }
                is ResultType.Success<*> -> {
                    val response = it.data as FeedbackResponse
                    if (response.status == "success") {
                        Toast.makeText(this@FeedBackActivity, response.status, Toast.LENGTH_SHORT)
                            .show()
                        binding.progressbar.visibility = View.GONE
                        binding.contentEditText.text?.clear()
                    }
                }
                is ResultType.Error -> {
                    Toast.makeText(
                        this@FeedBackActivity,
                        R.string.error_response_msg,
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.progressbar.visibility = View.GONE
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