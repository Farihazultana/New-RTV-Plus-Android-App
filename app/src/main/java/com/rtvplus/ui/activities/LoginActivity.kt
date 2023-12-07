package com.rtvplus.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.rtvplus.R
import com.rtvplus.data.models.logIn.SocialLoginData
import com.rtvplus.databinding.ActivityLoginBinding
import com.rtvplus.ui.viewmodels.ForgetPasswordViewModel
import com.rtvplus.ui.viewmodels.SharedViewModel
import com.rtvplus.utils.AppUtils
import com.rtvplus.utils.AppUtils.SignInType
import com.rtvplus.utils.AppUtils.UserPasswordKey
import com.rtvplus.utils.AppUtils.UsernameInputKey
import com.rtvplus.utils.AppUtils.isLoggedIn
import com.rtvplus.utils.LogInUtil
import com.rtvplus.utils.ResultType
import com.rtvplus.utils.SharedPreferencesUtil
import com.rtvplus.utils.SocialmediaLoginUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.json.JSONException


@AndroidEntryPoint
class LoginActivity : AppCompatActivity(), LogInUtil.ObserverListener, SocialmediaLoginUtil.ObserverListenerSocial{

    private lateinit var binding: ActivityLoginBinding
    private val forgetPasswordViewModel by viewModels<ForgetPasswordViewModel>()
    private var phoneText: String? = null
    private lateinit var dialog: Dialog

    private val _requestCodeSignIn = 1000
    private var gso: GoogleSignInOptions? = null
    private var gsc: GoogleSignInClient? = null

    private val callbackManager = CallbackManager.Factory.create()

    private lateinit var enteredPhone: String
    private lateinit var enteredPassword: String

    private val sharedViewModel: SharedViewModel by viewModels()

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(view)

        val logInUtil = LogInUtil()

        //Text Counter for Phone number 0/11
        //textCounter()


        //LogIn with phone
        binding.btnLogIn.setOnClickListener {

            handlePhoneLogin(logInUtil)
        }

        //phone login observe & socialMedia login observe
        logInUtil.observeLoginData(this, this, this, this)
        SocialmediaLoginUtil().observeSocialLogInData(this, this, this, this)

        //Forget Password
        forgetPassword()

        //Google Sign In
        binding.btnGoogleSignIn.setOnClickListener {

            googleLogIn()
        }

        //Facebook Signin
        // Trigger the Facebook login when the button is clicked
        binding.btnFacebookSignIn.setOnClickListener {

            facebookLoginIntegration()
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("public_profile"))
        }

        //Not Registered Click here
        binding.tvGoToRegistration.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }

    private fun facebookLoginIntegration() {
        // Facebook login callback
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onCancel() {
                    Log.i("FacebookProfile", "onCancel: ")
                }

                override fun onError(error: FacebookException) {
                    Log.i("FacebookProfile", "onError: $error")
                }

                override fun onSuccess(result: LoginResult) {
                    // Handle successful login
                    // After successful login, retrieve the user's profile information
                    Log.i("FacebookProfile", "onSuccess: ")
                    val accessToken = AccessToken.getCurrentAccessToken()
                    if (accessToken != null && !accessToken.isExpired) {
                        // Request the user's profile information
                        val request = GraphRequest.newMeRequest(accessToken) { obj, response ->
                            Log.i("FacebookProfile", "onSuccess: $response")
                            if (response?.error == null) {
                                try {
                                    val id = obj?.getString("id").toString()
                                    //val email = obj?.getString("email")
                                    val fullname = obj?.getString("name").toString()
                                    //val birthday = obj?.getString("birthday")

                                    // Access the profile picture URL
                                    val pictureObj = obj?.getJSONObject("picture")
                                    val pictureData = pictureObj?.getJSONObject("data")
                                    val profileImage = pictureData?.getString("url").toString()

                                    // Log the profile information
                                    Log.d("FacebookProfile", "ID: $id, Name: $fullname, Email: , Image URL: $profileImage")

                                    SharedPreferencesUtil.saveData(this@LoginActivity, UsernameInputKey, id ?: "")
                                    /*SharedPreferencesUtil.saveData(this@LoginActivity, AppUtils.FBSignIN_Fullname, fullname ?: "")
                                    SharedPreferencesUtil.saveData(this@LoginActivity, AppUtils.FBSignIn_ImgUri, profileImage ?: "")*/

                                    val loginData = SocialLoginData("", "", "", profileImage, fullname)
                                    SharedPreferencesUtil.saveSocialLogInData(this@LoginActivity, loginData)

                                    SocialmediaLoginUtil().fetchSocialLogInData(this@LoginActivity, "facebook",id,fullname, "","", profileImage )
                                }catch (e : JSONException){
                                    Log.e("FacebookProfile", "onSuccess: $e")
                                }

                            }else {
                                Log.e("FacebookProfile", "Error in GraphRequest: ${response.error}")
                            }
                        }
                        val parameters = Bundle()
                        parameters.putString("fields", "id,name,email,picture.type(large)")
                        request.parameters = parameters
                        request.executeAsync()

                    }
                }
            })
    }

    private fun handlePhoneLogin(logInUtil: LogInUtil) {
        enteredPhone = binding.etPhoneText.text.toString()
        enteredPassword = binding.etPasswordText.text.toString()

        if (enteredPhone.isNotEmpty() && enteredPassword.isNotEmpty() && enteredPhone.length == 11) {
            phoneText = "88$enteredPhone"

            logInUtil.fetchLogInData(this, phoneText!!, enteredPassword)

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

    private fun forgetPassword() {
        forgetPasswordObserve()
        binding.tvForgotPassword.setOnClickListener {
            setDialog()
            val btnSendRequest = dialog.findViewById<Button>(R.id.btnSendRequest)
            forgetAction(btnSendRequest)
        }
    }

    private fun forgetAction(button: Button) {
        button.setOnClickListener {
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

        dialog.show()
    }


    private fun setDialog() {
        dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_forget_password)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(true)
        dialog.window!!.attributes!!.windowAnimations = R.style.animation


    }

    private fun forgetPasswordApiCall(phoneText: String) {
        forgetPasswordViewModel.fetchForgetPasswordData(phoneText, "forget")
    }

    private fun forgetPasswordObserve() {
        lifecycleScope.launch {
            forgetPasswordViewModel.forgetPasswordData.observe(this@LoginActivity) {
                when (it) {
                    is ResultType.Success -> {
                        val result = it.data
                        Toast.makeText(this@LoginActivity, result.message, Toast.LENGTH_LONG).show()
                    }

                    is ResultType.Error -> {
                        Toast.makeText(this@LoginActivity, "Something is wrong!", Toast.LENGTH_LONG)
                            .show()
                    }

                    else -> {

                    }
                }
            }
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            _requestCodeSignIn -> {
                if (resultCode != RESULT_OK) {
                    // User canceled the sign-in.
                    Toast.makeText(this@LoginActivity, "Google Sign-In was canceled by the user", Toast.LENGTH_LONG).show()
                    return
                }

                val task = gsc!!.silentSignIn()
                task.addOnCompleteListener { task ->
                    try {
                        if (task.isSuccessful) {
                            // Successful sign-in
                            updateViewWithAccount()
                        } else {
                            // Handle other cases or show an error message to the user.
                            val exception = task.exception
                            if (exception is ApiException) {
                                val statusCode = exception.statusCode
                                when (statusCode) {
                                    GoogleSignInStatusCodes.SIGN_IN_CANCELLED -> {
                                        // User canceled the sign-in.
                                        Toast.makeText(this@LoginActivity, "Google Sign-In was canceled by the user", Toast.LENGTH_LONG).show()
                                    }

                                    GoogleSignInStatusCodes.SIGN_IN_FAILED -> {
                                        // Sign-in failed for some reason.
                                        // Update UI accordingly or show an error message.
                                        Toast.makeText(this@LoginActivity, "Google Sign-In failed. Please try again later.", Toast.LENGTH_LONG).show()
                                    }

                                    else -> {
                                        // Handle unknown errors or show a generic error message.
                                        Toast.makeText(
                                            this@LoginActivity,
                                            "Google Sign-In encountered an error. Please try again later.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            }
                        }
                    } catch (apiException: ApiException) {
                        Toast.makeText(
                            this@LoginActivity,
                            "An error occurred during Google Sign-In. Please try again.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        //facebook
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    private fun updateViewWithAccount() {
        val acct = GoogleSignIn.getLastSignedInAccount(this)
        if (acct != null) {
            val displayName = acct.displayName
            val personEmail = acct.email
            val firstname = acct.givenName
            val lastname = acct.familyName
            val imageUri = acct.photoUrl
            val userID = acct.id

            val loginData = SocialLoginData(personEmail.toString(), firstname.toString(), lastname.toString(), imageUri.toString(), displayName.toString())

            Log.i("SignIn", "onActivityResult: $displayName $personEmail, $userID, $firstname, $lastname, $imageUri")

            SharedPreferencesUtil.saveData(this@LoginActivity, UsernameInputKey, userID ?: "")

            SharedPreferencesUtil.saveSocialLogInData(this, loginData)

            SocialmediaLoginUtil().fetchSocialLogInData(this, "google", userID!!, firstname!!, lastname!!, personEmail!!, imageUri.toString())


        }
    }

    override fun observerListener(result: String) {
        if (result == "success") {
            SharedPreferencesUtil.saveData(this, UsernameInputKey, phoneText!!)
            SharedPreferencesUtil.saveData(this, UserPasswordKey, enteredPassword)
            SharedPreferencesUtil.saveData(this, SignInType, "Phone")



            //Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Username or Password incorrect. Try Again!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun observerListenerSocial(result: String, loginSrc: String) {
        if (result == "success") {
            if (loginSrc == AppUtils.Type_google){
                SharedPreferencesUtil.saveData(this, SignInType, AppUtils.Type_google)
            }else if (loginSrc == AppUtils.Type_fb){
                SharedPreferencesUtil.saveData(this, SignInType, AppUtils.Type_fb)
            }



            finish()
        }
    }


    override fun finish() {
        super.finish()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        sharedViewModel.flagLiveData.value = true
        finish()
    }




}