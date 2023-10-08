package com.example.rtv_plus_android_app_revamp.ui.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.rtv_plus_android_app_revamp.R
import com.example.rtv_plus_android_app_revamp.databinding.ActivityLoginBinding
import com.example.rtv_plus_android_app_revamp.ui.fragments.HomeFragment
import com.example.rtv_plus_android_app_revamp.ui.viewmodels.ForgetPasswordViewModel
import com.example.rtv_plus_android_app_revamp.ui.viewmodels.GoogleLogInViewModel
import com.example.rtv_plus_android_app_revamp.ui.viewmodels.LogInViewModel
import com.example.rtv_plus_android_app_revamp.utils.AppUtils.GoogleSignInKey
import com.example.rtv_plus_android_app_revamp.utils.AppUtils.LogInKey
import com.example.rtv_plus_android_app_revamp.utils.AppUtils.PhoneInputKey
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import com.example.rtv_plus_android_app_revamp.utils.SharedPreferencesUtil
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val logInViewModel by viewModels<LogInViewModel>()
    private val forgetPasswordViewModel by viewModels<ForgetPasswordViewModel>()
    private val googleLogInViewModel by viewModels<GoogleLogInViewModel>()
    private var phoneText: String? = null
    private lateinit var dialog: Dialog
    private lateinit var googleSignInOptions: GoogleSignInOptions
    private lateinit var googleSignInClient: GoogleSignInClient
    private val _requestCodeSignIn = 1000
    lateinit var oneTapClient: SignInClient
    lateinit var signUpRequest: BeginSignInRequest

    companion object {
        var showOneTapUI = true
    }

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //Text Counter for Phone number 0/11
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

        //LogIn with phone
        binding.btnLogIn.setOnClickListener {
            val enteredPhone = binding.etPhoneText.text.toString()
            val enteredPassword = binding.etPasswordText.text.toString()

            if (enteredPhone.isNotEmpty() || enteredPassword.isNotEmpty()) {
                if (enteredPhone.length == 11) {
                    phoneText = "88$enteredPhone"
                    Log.i("TagP", "Phone Input from EditText: $phoneText")

                    if (enteredPassword.isNotEmpty()) {
                        logInViewModel.fetchLogInData(phoneText!!, "123457", "yes", "1")
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Password can't be empty! Please input first.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Phone number should be 11 digits",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
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
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)
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

        // Forget Password
        dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_forget_password)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(true)
        dialog.window!!.attributes!!.windowAnimations = R.style.animation

        val btnSendRequest = dialog.findViewById<Button>(R.id.btnSendRequest)

        binding.tvForgotPassword.setOnClickListener {
            dialog.show()
            btnSendRequest?.setOnClickListener {
                val enteredUsername =
                    dialog.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etUsername).text.toString()

                if (enteredUsername.isNotEmpty()) {
                    if (enteredUsername.length == 11) {
                        val phoneText = "88$enteredUsername"
                        forgetPasswordViewModel.fetchForgetPasswordData(
                            phoneText,
                            "123457",
                            "123456"
                        )
                    } else if (enteredUsername.length < 11) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Please type valid mobile number",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Mobile number size is invalid - 13",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Please type valid mobile number",
                        Toast.LENGTH_LONG
                    ).show()
                }

                lifecycleScope.launch {
                    forgetPasswordViewModel.forgetPasswordData.collect {
                        when (it) {
                            is ResultType.Success -> {
                                val result = it.data
                                Toast.makeText(this@LoginActivity, result.status, Toast.LENGTH_LONG)
                                    .show()
                            }

                            is ResultType.Error -> {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Something is wrong, please try again!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                            else -> {

                            }
                        }
                    }
                }
            }
        }

        //Google Sign In

        binding.btnGoogleSignIn.setOnClickListener {
            if (showOneTapUI) {
                oneTapClient = Identity.getSignInClient(this)
                signUpRequest = BeginSignInRequest.builder()
                    .setGoogleIdTokenRequestOptions(
                        BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                            .setSupported(true)
                            // Your server's client ID, not your Android client ID.
                            .setServerClientId(getString(R.string.web_client_id))
                            // Show all accounts on the device.
                            .setFilterByAuthorizedAccounts(false)
                            .build()
                    )
                    .build()

                oneTapClient.beginSignIn(signUpRequest)
                    .addOnSuccessListener(this@LoginActivity) { result ->
                        try {
                            startIntentSenderForResult(
                                result.pendingIntent.intentSender, _requestCodeSignIn,
                                null, 0, 0, 0
                            )
                            Log.i("OneTap", "Successful")

                        } catch (e: IntentSender.SendIntentException) {
                            Log.e(TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
                            Log.i("OneTap", "Couldn't start One Tap UI: ${e.localizedMessage}")
                        }
                    }
                    .addOnFailureListener(this) { e ->
                        // No Google Accounts found. Just continue presenting the signed-out UI.
                        Log.d(TAG, e.localizedMessage!!)
                        Log.i(
                            "OneTap",
                            "addOnFailureListener : No Google Accounts found. Just continue presenting the signed-out UI : ${e.localizedMessage}"
                        )
                    }
            } else {
                Toast.makeText(this@LoginActivity, "Try after 30 seconds!", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        Handler().postDelayed({
            showOneTapUI = true
            Log.i("OneTap", "One Tap re-enabled.")
        }, 30000)

        //Not Registered Click here
        binding.tvGoToRegistration.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            _requestCodeSignIn -> {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken
                    val username = credential.id
                    val password = credential.password
                    val displayName = credential.displayName
                    val givenName = credential.givenName
                    val familyName = credential.familyName
                    val imageUri = credential.profilePictureUri
                    when {
                        idToken != null -> {
                            // Got an ID token from Google. Use it to authenticate
                            // with your backend.
                            Log.d(TAG, "Got ID token.")
                            Log.i("OneTap", "Got ID token. $idToken")
                            Log.i("OneTap", "Got user email. $username")
                            Log.i("OneTap", "Got user display name. $displayName")
                            Log.i("OneTap", "Got user given name. $givenName")
                            Log.i("OneTap", "Got user family name. $familyName")
                            Log.i("OneTap", "Got user image uri. $imageUri")
                            SharedPreferencesUtil.saveData(
                                this@LoginActivity,
                                GoogleSignInKey,
                                username
                            )
                            googleLogInViewModel.fetchGoogleLogInData(
                                "social",
                                "google",
                                idToken,
                                "",
                                givenName!!,
                                familyName!!,
                                username,
                                imageUri.toString()
                            )
                            lifecycleScope.launch {
                                googleLogInViewModel.googleLogInData.collect {
                                    when (it) {
                                        is ResultType.Success -> {
                                            val result = it.data
                                            for (i in result) {
                                                i.result
                                                Toast.makeText(
                                                    this@LoginActivity,
                                                    i.result,
                                                    Toast.LENGTH_LONG
                                                ).show()

                                            }

                                        }

                                        is ResultType.Error -> {
                                            Toast.makeText(
                                                this@LoginActivity,
                                                "Something is wrong, please try again!",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }

                                        else -> {

                                        }
                                    }
                                }
                            }
                            Handler().postDelayed({ finish() }, 2000)

                        }

                        password != null -> {
                            // Got a saved username and password. Use them to authenticate
                            // with your backend.
                            Log.d(TAG, "Got password.")
                            Log.i("OneTap", "Got password. $password")
                        }

                        else -> {
                            // Shouldn't happen.
                            Log.i("OneTap", "No ID token or password!")
                        }
                    }
                } catch (e: ApiException) {
                    // ...
                }
            }
        }
    }

}