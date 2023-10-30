package com.rtvplus.ui.activities

import LogInUtil
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
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
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.rtvplus.utils.AppUtils.SignInType
import com.rtvplus.utils.AppUtils.UserPasswordKey
import com.rtvplus.utils.AppUtils.UsernameInputKey
import com.rtvplus.utils.SocialmediaLoginUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LoginActivity : AppCompatActivity(), LogInUtil.ObserverListener,
    SocialmediaLoginUtil.ObserverListenerSocial {

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

    var gso: GoogleSignInOptions? = null
    var gsc: GoogleSignInClient? = null



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

        var logInUtil = LogInUtil()

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
                logInUtil.fetchLogInData(this, phoneText!!, enteredPassword)
                SharedPreferencesUtil.saveData(this, SignInType, "Phone")
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
            SharedPreferencesUtil.saveData(this, SignInType, "Google")
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
        // Initialize GoogleSignInOptions
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id)) // Use your web client ID
            .requestEmail()
            .build()

        // Initialize GoogleSignInClient
        gsc = GoogleSignIn.getClient(this, gso!!)

        // gsc to get the sign-in intent
        val signInIntent = gsc!!.signInIntent
        startActivityForResult(signInIntent, _requestCodeSignIn)
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
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    task.getResult(ApiException::class.java)
                    val acct = GoogleSignIn.getLastSignedInAccount(this)
                    if (acct != null) {
                        val personName = acct.displayName
                        val personEmail = acct.email
                        val authId = acct.idToken
                        val firstname = acct.givenName
                        val lastname = acct.familyName
                        val imageUri = acct.photoUrl
                        val userID = acct.id

                        Log.i(
                            "SignIn",
                            "onActivityResult: $personEmail, $authId, $firstname, $lastname, $imageUri"
                        )

                        SharedPreferencesUtil.saveData(
                            this@LoginActivity,
                            UsernameInputKey,
                            userID!!
                        )

                        SocialmediaLoginUtil().fetchGoogleLogInData(
                            this,
                            userID!!,
                            firstname!!,
                            lastname!!,
                            personEmail!!,
                            imageUri.toString()
                        )
                        SocialmediaLoginUtil().observeGoogleLogInData(this, this, this, this)

                    }
                    //finish()
                } catch (e: ApiException) {
                    Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
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

    override fun observerListenerSocial(result: String) {
        if (result == "success") {
            finish()
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