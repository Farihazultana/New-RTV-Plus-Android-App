package com.rtvplus.utils

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
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
        val loginResult = socialLoginResult.data[0]
        SharedPreferencesUtil.saveData(context, AppUtils.LogIn_audioad, loginResult.audioad ?: "")
        SharedPreferencesUtil.saveData(context, AppUtils.LogIn_concurrent, loginResult.concurrent ?: "")
        SharedPreferencesUtil.saveData(context, AppUtils.LogIn_concurrenttext, loginResult.concurrenttext ?: "")
        SharedPreferencesUtil.saveData(context, AppUtils.LogIn_consent, loginResult.consent ?: "")
        SharedPreferencesUtil.saveData(context, AppUtils.LogIn_consenttext, loginResult.consenttext ?: "")
        SharedPreferencesUtil.saveData(context, AppUtils.LogIn_consenturl, loginResult.consenturl ?: "")
        SharedPreferencesUtil.saveData(context, AppUtils.LogIn_currentversion, loginResult.currentversion ?: "")
        SharedPreferencesUtil.saveData(context, AppUtils.LogIn_currentversionios, loginResult.currentversionios ?: "")
        SharedPreferencesUtil.saveData(context, AppUtils.LogIn_email, loginResult.email ?: "")
        SharedPreferencesUtil.saveData(context, AppUtils.LogIn_enforce, loginResult.enforce ?: "")
        SharedPreferencesUtil.saveData(context, AppUtils.LogIn_enforcetext, loginResult.enforcetext ?: "")
        SharedPreferencesUtil.saveData(context, AppUtils.LogIn_extrainfo, loginResult.extrainfo ?: "")
        SharedPreferencesUtil.saveData(context, AppUtils.LogIn_fullname, loginResult.fullname ?: "")
        SharedPreferencesUtil.saveData(context, AppUtils.LogIn_liveurl, loginResult.liveurl ?: "")
        SharedPreferencesUtil.saveData(context, AppUtils.LogIn_msisdn, loginResult.msisdn ?: "")
        SharedPreferencesUtil.saveData(context, AppUtils.LogIn_packcode, loginResult.packcode ?: "")
        SharedPreferencesUtil.saveData(context, AppUtils.LogIn_packname, loginResult.packname ?: "")
        SharedPreferencesUtil.saveData(context, AppUtils.LogIn_packtext, loginResult.packtext ?: "")
        SharedPreferencesUtil.saveData(context, AppUtils.LogIn_play, loginResult.play ?: "")
        SharedPreferencesUtil.saveData(context, AppUtils.LogIn_referralimage, loginResult.referralimage ?: "")
        SharedPreferencesUtil.saveData(context, AppUtils.LogIn_result, loginResult.result ?: "")
        SharedPreferencesUtil.saveData(context, AppUtils.LogIn_showad, loginResult.showad ?: "")
        SharedPreferencesUtil.saveData(context, AppUtils.LogIn_token, loginResult.token ?: "")

    }

}