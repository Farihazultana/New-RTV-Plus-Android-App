package com.example.rtv_plus_android_app_revamp.ui.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.rtv_plus_android_app_revamp.databinding.ActivityLoginBinding
import com.example.rtv_plus_android_app_revamp.ui.viewmodels.LogInViewModel
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import com.example.rtv_plus_android_app_revamp.utils.SharedPreferencesUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val logInViewModel by viewModels<LogInViewModel>()
    var logInResultData: String?=null
    companion object{
        val LogInKey= "LogIn_Result"
        val PhoneInput = "PhoneKey"
    }


    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        val phoneText = SharedPreferencesUtil.getData(this@LoginActivity, PhoneInput, "default_value")
        Log.i("TagP", "onCreate: $phoneText")

        binding.btnLogIn.setOnClickListener {
            logInViewModel.fetchLogInData(phoneText.toString(), "123457", "no")

        }



        lifecycleScope.launch {
            logInViewModel.logInData.collect {
                when (it) {
                    is ResultType.Success -> {
                        val logInResult = it.data
                        for (item in logInResult) {
                            val result = item.result
                            logInResultData=result
                            Log.i("LogIN", "onCreate: $result")
                            SharedPreferencesUtil.saveData(this@LoginActivity, LogInKey, result)
                            val phoneInput = binding.etPhoneText.text.toString()
                            Log.i("PhoneInput", "Phone Input : $phoneInput")
                            SharedPreferencesUtil.saveData(this@LoginActivity, PhoneInput,phoneInput)
                        }

                    }

                    is ResultType.Error -> {
                        Toast.makeText(
                            this@LoginActivity,
                            "Something is wrong, please try again!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {}
                }
            }
        }

        binding.tvGoToRegistration.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegistrationActivity::class.java)
            startActivity(intent)
        }



    }
}