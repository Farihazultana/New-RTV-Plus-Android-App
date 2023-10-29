package com.rtvplus.utils

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.rtvplus.data.models.logIn.LogInResponseItem
import com.rtvplus.data.models.socialmedia_login.google.GoogleLogInResponse
import com.rtvplus.ui.viewmodels.GoogleLogInViewModel

class SocialmediaLoginUtil {
    interface ObserverListenerSocial {
        fun observerListenerSocial(result: String)
    }

    lateinit var observerListenerGoogle: ObserverListenerSocial

    fun fetchGoogleLogInData(viewModelStoreOwner: ViewModelStoreOwner,username: String, firstName: String, lastName: String, email: String, imgUrl: String){
        val googleLogInViewModel = ViewModelProvider(viewModelStoreOwner)[GoogleLogInViewModel::class.java]
        googleLogInViewModel.fetchGoogleLogInData("social", "google", username, "", firstName, lastName,email, imgUrl)
    }

    fun observeGoogleLogInData(context: Context, lifecycleOwner: LifecycleOwner, viewModelStoreOwner: ViewModelStoreOwner, listener : ObserverListenerSocial){
        val googleLogInViewModel = ViewModelProvider(viewModelStoreOwner)[GoogleLogInViewModel::class.java]

        this.observerListenerGoogle = listener
        googleLogInViewModel.googleLogInData.observe(lifecycleOwner) {socialLoginResult ->
            when (socialLoginResult) {
                is ResultType.Success -> {
                    val result = socialLoginResult.data[0].result
                    Toast.makeText(context, result, Toast.LENGTH_LONG).show()
                    Log.i("OneTap", "observeGoogleLogInData Packcode: ${socialLoginResult.data[0].packcode}")

                    this.observerListenerGoogle.observerListenerSocial(result)

                    saveGoogleLoginData(context, socialLoginResult)
                }

                is ResultType.Error -> {
                    Toast.makeText(
                        context,
                        "Something is wrong, please try again!",
                        Toast.LENGTH_LONG
                    ).show()
                }

                else -> {
                    Log.i(ContentValues.TAG, "onActivityResult: Login data not available")
                }
            }
        }
    }

    private fun saveGoogleLoginData(context: Context, socialLoginResult: ResultType.Success<GoogleLogInResponse>) {
        SharedPreferencesUtil.saveData(context, AppUtils.LogIn_audioad, socialLoginResult.data[0].audioad ?: "")
        SharedPreferencesUtil.saveData(context, AppUtils.LogIn_concurrent, socialLoginResult.data[0].concurrent ?: "")
        SharedPreferencesUtil.saveData(context, AppUtils.LogIn_concurrenttext, socialLoginResult.data[0].concurrenttext ?: "")
        SharedPreferencesUtil.saveData(context, AppUtils.LogIn_consent, socialLoginResult.data[0].consent ?: "")
        SharedPreferencesUtil.saveData(context, AppUtils.LogIn_consenttext, socialLoginResult.data[0].consenttext ?: "")
        SharedPreferencesUtil.saveData(context, AppUtils.LogIn_consenturl, socialLoginResult.data[0].consenturl ?: "")
        SharedPreferencesUtil.saveData(context, AppUtils.LogIn_currentversion, socialLoginResult.data[0].currentversion ?: "")
        SharedPreferencesUtil.saveData(context, AppUtils.LogIn_currentversionios, socialLoginResult.data[0].currentversionios ?: "")
        SharedPreferencesUtil.saveData(context, AppUtils.LogIn_email, socialLoginResult.data[0].email ?: "")
        SharedPreferencesUtil.saveData(context, AppUtils.LogIn_enforce, socialLoginResult.data[0].enforce ?: "")
        SharedPreferencesUtil.saveData(context, AppUtils.LogIn_enforcetext, socialLoginResult.data[0].enforcetext ?: "")
        SharedPreferencesUtil.saveData(context, AppUtils.LogIn_extrainfo, socialLoginResult.data[0].extrainfo ?: "")
        SharedPreferencesUtil.saveData(context, AppUtils.LogIn_fullname, socialLoginResult.data[0].fullname ?: "")
        SharedPreferencesUtil.saveData(context, AppUtils.LogIn_liveurl, socialLoginResult.data[0].liveurl ?: "")
        SharedPreferencesUtil.saveData(context, AppUtils.LogIn_msisdn, socialLoginResult.data[0].msisdn ?: "")
        SharedPreferencesUtil.saveData(context, AppUtils.LogIn_packcode, socialLoginResult.data[0].packcode ?: "")
        SharedPreferencesUtil.saveData(context, AppUtils.LogIn_packname, socialLoginResult.data[0].packname ?: "")
        SharedPreferencesUtil.saveData(context, AppUtils.LogIn_packtext, socialLoginResult.data[0].packtext ?: "")
        SharedPreferencesUtil.saveData(context, AppUtils.LogIn_play, socialLoginResult.data[0].play ?: "")
        SharedPreferencesUtil.saveData(context, AppUtils.LogIn_referralimage, socialLoginResult.data[0].referralimage ?: "")
        SharedPreferencesUtil.saveData(context, AppUtils.LogIn_result, socialLoginResult.data[0].result ?: "")
        SharedPreferencesUtil.saveData(context, AppUtils.LogIn_showad, socialLoginResult.data[0].showad ?: "")
        SharedPreferencesUtil.saveData(context, AppUtils.LogIn_token, socialLoginResult.data[0].token ?: "")

    }

}