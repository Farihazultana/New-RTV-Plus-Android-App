package com.example.rtv_plus_android_app_revamp.ui.activities

import android.annotation.SuppressLint
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
    private var phoneText: String? = null

    companion object {
        const val LogInKey = "LogIn_Result"
        const val PhoneInputKey = "PhoneKey"
    }

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnLogIn.setOnClickListener {
            val enteredPhone = binding.etPhoneText.text.toString()
            val enteredPassword = binding.etPasswordText.text.toString()

            if (enteredPhone.isNotEmpty() || enteredPassword.isNotEmpty()){
                if (enteredPhone.length == 11) {
                    phoneText = "88$enteredPhone"
                    Log.i("TagP", "Phone Input from EditText: $phoneText")

                    if (enteredPassword.isNotEmpty()) {
                        logInViewModel.fetchLogInData(phoneText!!, "123457", "yes", "1")
                    }
                    else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Password can't be empty! Please input first.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Phone number should be 11 digits",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            else {
                Toast.makeText(
                    this@LoginActivity,
                    "Phone number & Password can't be empty! Please input first.",
                    Toast.LENGTH_LONG
                ).show()
            }


        }


        lifecycleScope.launch {
            logInViewModel.logInData.collect {
                when (it) {
                    is ResultType.Success -> {
                        val logInResult = it.data
                        for (item in logInResult) {
                            val result = item.result
                            Log.i("LogIN", "Log In result to be saved: $result")
                            SharedPreferencesUtil.saveData(this@LoginActivity, LogInKey, result)

                            if (result == "success") {
                                Log.i("TAGP", "LogIn: $result")
                                SharedPreferencesUtil.saveData(
                                    this@LoginActivity,
                                    PhoneInputKey,
                                    phoneText!!
                                ).toString()
                                finish()
//                                val getPhoneNumSP = SharedPreferencesUtil.getData(
//                                    this@LoginActivity,
//                                    PhoneInputKey,
//                                    "defaultValue"
//                                )
//                                Log.i("PhoneInput", "Saved Phone Input from SP : $getPhoneNumSP")
                            } else {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Username or Password incorrect. Try Again!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
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