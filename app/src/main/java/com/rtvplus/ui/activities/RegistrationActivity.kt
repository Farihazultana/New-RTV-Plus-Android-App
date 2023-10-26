package com.rtvplus.ui.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.rtvplus.databinding.ActivityRegistrationBinding
import com.rtvplus.ui.viewmodels.RegistrationViewModel
import com.rtvplus.utils.ResultType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding
    private val registrationViewModel by viewModels<RegistrationViewModel>()
    private var phoneText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.etPhoneText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val charCount = p0?.length ?: 0
                binding.tvInputCounter.text = "$charCount/11"

            }

            override fun afterTextChanged(p0: Editable?) {

            }
        }
        )

        binding.btnRegistration.setOnClickListener {
            val enteredPhone = binding.etPhoneText.text.toString()
            if (enteredPhone.length != 11 || enteredPhone.isEmpty()) {
                Toast.makeText(
                    this@RegistrationActivity,
                    "Please enter valid mobile number",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                phoneText = "88$enteredPhone"
                registrationViewModel.fetchRegistrationData(phoneText!!)
            }
        }

        lifecycleScope.launch {
            registrationViewModel.registrationData.observe(this@RegistrationActivity) {
                when (it) {
                    is ResultType.Success -> {
                        val registratResult = it.data
                        val result = registratResult.message
                        Toast.makeText(this@RegistrationActivity, result, Toast.LENGTH_LONG).show()
                    }
                    is ResultType.Error -> {
                        Toast.makeText(
                            this@RegistrationActivity,
                            "Something is wrong, please try again!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {

                    }
                }
            }
        }

        binding.tvGoToLogIn.setOnClickListener {
            val intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }


}