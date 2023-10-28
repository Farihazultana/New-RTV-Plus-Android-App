package com.rtvplus.ui.activities

import LogInUtil
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.rtvplus.R
import com.rtvplus.databinding.ActivityLoginBinding
import com.rtvplus.ui.viewmodels.ForgetPasswordViewModel
import com.rtvplus.ui.viewmodels.GoogleLogInViewModel
import com.rtvplus.ui.viewmodels.LogInViewModel
import com.rtvplus.utils.ResultType
import com.rtvplus.utils.SharedPreferencesUtil
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.rtvplus.utils.AppUtils.UserPasswordKey
import com.rtvplus.utils.AppUtils.UsernameInputKey
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class LoginActivity : AppCompatActivity(), LogInUtil.ObserverListener {

    private lateinit var binding: ActivityLoginBinding
    private val logInViewModel by viewModels<LogInViewModel>()
    private val forgetPasswordViewModel by viewModels<ForgetPasswordViewModel>()
    private val googleLogInViewModel by viewModels<GoogleLogInViewModel>()
    private var phoneText: String? = null
    private lateinit var dialog: Dialog

    private val _requestCodeSignIn = 1000
    lateinit var oneTapClient: SignInClient
    lateinit var signUpRequest: BeginSignInRequest

    lateinit var enteredPhone: String
    lateinit var enteredPassword: String

//    @Inject
//    lateinit var loginInfo: LogInModule


    companion object {
        var showOneTapUI = true
        var packCode: String? = null
        var packText: String? = null
    }

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        var logInUtil  = LogInUtil()

        //Text Counter for Phone number 0/11
        textCounter()


        //LogIn with phone
        binding.btnLogIn.setOnClickListener {
            enteredPhone = binding.etPhoneText.text.toString()
            enteredPassword = binding.etPasswordText.text.toString()
            Log.i("Newton", "onCreate: LogIn Button clicked!")

            if (enteredPhone.isNotEmpty() && enteredPassword.isNotEmpty() && enteredPhone.length == 11) {
                phoneText = "88$enteredPhone"
                //logInViewModel.fetchLogInData(phoneText!!, enteredPassword!!, "no", "1")
                //MainActivity().fetchLogInData(phoneText!!, enteredPassword!!)
                logInUtil.fetchLogInData(this,phoneText!!, enteredPassword)

            } else {
                if (enteredPhone.isEmpty() || enteredPassword.isEmpty()) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Phone number or Password must not be empty! Please input first.",
                        Toast.LENGTH_LONG
                    ).show()

                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Phone number should be 11 digits",
                        Toast.LENGTH_LONG
                    ).show()

                }

            }

        }



        logInUtil.observeLoginData(this, this, this, this)

        forgetPassword()

        //Google Sign In
        binding.btnGoogleSignIn.setOnClickListener {
            showOneTapUI = true
            if (showOneTapUI) {
                googleLogIn()
            } else {
                Toast.makeText(
                    this@LoginActivity,
                    "Something went wrong! Please try again later..",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }

        //Not Registered Click here
        binding.tvGoToRegistration.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }


    private fun textCounter() {
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
    }

    private fun googleLogIn() {
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
    }

    private fun forgetPassword() {
        openDialog()
        forgetPasswordObserve()

        val btnSendRequest = dialog.findViewById<Button>(R.id.btnSendRequest)

        binding.tvForgotPassword.setOnClickListener {
            dialog.show()
        }
        btnSendRequest?.setOnClickListener {
            val enteredUsername =
                dialog.findViewById<TextInputEditText>(R.id.etUsername).text.toString()
            Log.i("Forget", "onCreate: $enteredUsername")

            if (enteredUsername.isNotEmpty() && enteredUsername.length == 11) {
                val phoneText = "88$enteredUsername"
                forgetPasswordApiCall(phoneText)
            } else {
                if (enteredUsername.isEmpty()) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Phone number can't be empty!",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Please type valid mobile number",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        }

    }

    private fun forgetPasswordApiCall(phoneText: String) {
        forgetPasswordViewModel.fetchForgetPasswordData(
            phoneText,
            "forget",
        )
    }

    private fun forgetPasswordObserve() {
        lifecycleScope.launch {
            forgetPasswordViewModel.forgetPasswordData.observe(this@LoginActivity) {
                when (it) {
                    is ResultType.Success -> {
                        val result = it.data
                        Toast.makeText(
                            this@LoginActivity,
                            result.message,
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }

                    is ResultType.Error -> {
                        Toast.makeText(
                            this@LoginActivity,
                            "Something is wrong!",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    else -> {

                    }
                }
            }
        }
    }

    private fun openDialog() {
        dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_forget_password)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(true)
        dialog.window!!.attributes!!.windowAnimations = R.style.animation
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
                                UsernameInputKey,
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
                                googleLogInViewModel.googleLogInData.observe(this@LoginActivity) {
                                    when (it) {
                                        is ResultType.Success -> {
                                            val result = it.data
                                            for (i in result) {
                                                i.result
                                                Toast.makeText(
                                                    this@LoginActivity,
                                                    i.result,
                                                    Toast.LENGTH_LONG
                                                )
                                                    .show()
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
                                            Log.i(TAG, "onActivityResult: Login data not available")
                                        }
                                    }
                                }
                            }
                            finish()


                        }

                        password != null -> {
                            // Got a saved username and password. Use them to authenticate with your backend.
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

    override fun observerListener(result: String) {
        Log.i("Newton", "observerListener: $result")
        if (result == "success") {
            SharedPreferencesUtil.saveData(
                this,
                UsernameInputKey,
                phoneText!!
            )
            SharedPreferencesUtil.saveData(this, UserPasswordKey, enteredPassword)
            Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(
                this,
                "Username or Password incorrect. Try Again!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun finish() {
        super.finish()
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}