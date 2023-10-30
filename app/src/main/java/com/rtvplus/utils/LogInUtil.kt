package com.rtvplus.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.rtvplus.data.models.logIn.LogInResponseItem
import com.rtvplus.ui.viewmodels.LogInViewModel


class LogInUtil {
    interface ObserverListener {
        fun observerListener(result: String)
    }

    private lateinit var observerListener: ObserverListener


    fun fetchLogInData(viewModelOwner: ViewModelStoreOwner, phoneText: String, enteredPassword: String) {
        val logInViewModel = ViewModelProvider(viewModelOwner)[LogInViewModel::class.java]
        logInViewModel.fetchLogInData(phoneText, enteredPassword, "no", "1")
    }

    fun observeLoginData(context: Context, lifecycleOwner: LifecycleOwner, viewModelOwner: ViewModelStoreOwner, listener: ObserverListener) {
        val logInViewModel = ViewModelProvider(viewModelOwner)[LogInViewModel::class.java]
        var result: String
        this.observerListener = listener

        logInViewModel.logInData.observe(lifecycleOwner) {
            when (it) {
                is ResultType.Loading -> {

                }

                is ResultType.Success -> {
                    val logInResult = it.data[0]
                    result = logInResult.result
                    Log.i("Newton", "ResultType success observeLoginData: $result")

                    this.observerListener.observerListener(result)
                    SharedPreferencesUtil.saveData(context, AppUtils.LogInKey, result)

                    // Store Login Data
                    saveLoginData(context, logInResult)
                }

                is ResultType.Error -> {
                    Toast.makeText(context, "Username or Password incorrect. Try Again!", Toast.LENGTH_SHORT).show()
                }

                else -> {
                }

            }
        }

    }

    private fun saveLoginData(context: Context, logInResult: LogInResponseItem) {
        try {
            SharedPreferencesUtil.saveData(context, AppUtils.LogIn_audioad, logInResult.audioad)
            //SharedPreferencesUtil.saveData(context, AppUtils.LogIn_autorenew, logInResult.autorenew!! ?: "")
            SharedPreferencesUtil.saveData(context, AppUtils.LogIn_concurrent, logInResult.concurrent)
            SharedPreferencesUtil.saveData(context, AppUtils.LogIn_concurrenttext, logInResult.concurrenttext)
            SharedPreferencesUtil.saveData(context, AppUtils.LogIn_consent, logInResult.consent)
            SharedPreferencesUtil.saveData(context, AppUtils.LogIn_consenttext, logInResult.consenttext)
            SharedPreferencesUtil.saveData(context, AppUtils.LogIn_consenturl, logInResult.consenturl)
            SharedPreferencesUtil.saveData(context, AppUtils.LogIn_currentversion, logInResult.currentversion)
            SharedPreferencesUtil.saveData(context, AppUtils.LogIn_currentversionios, logInResult.currentversionios)
            SharedPreferencesUtil.saveData(context, AppUtils.LogIn_email, logInResult.email)
            SharedPreferencesUtil.saveData(context, AppUtils.LogIn_enforce, logInResult.enforce)
            SharedPreferencesUtil.saveData(context, AppUtils.LogIn_enforcetext, logInResult.enforcetext)
            SharedPreferencesUtil.saveData(context, AppUtils.LogIn_extrainfo, logInResult.extrainfo)
            //SharedPreferencesUtil.saveData(context, AppUtils.LogIn_fullname, logInResult.fullname!! ?: "")
            SharedPreferencesUtil.saveData(context, AppUtils.LogIn_liveurl, logInResult.liveurl)
            SharedPreferencesUtil.saveData(context, AppUtils.LogIn_msisdn, logInResult.msisdn)
            SharedPreferencesUtil.saveData(context, AppUtils.LogIn_packcode, logInResult.packcode)
            SharedPreferencesUtil.saveData(context, AppUtils.LogIn_packname, logInResult.packname)
            SharedPreferencesUtil.saveData(context, AppUtils.LogIn_packtext, logInResult.packtext)
            SharedPreferencesUtil.saveData(context, AppUtils.LogIn_play, logInResult.play)
            SharedPreferencesUtil.saveData(context, AppUtils.LogIn_referral, logInResult.referral)
            SharedPreferencesUtil.saveData(context, AppUtils.LogIn_referralimage, logInResult.referralimage)
            SharedPreferencesUtil.saveData(context, AppUtils.LogIn_result, logInResult.result)
            SharedPreferencesUtil.saveData(context, AppUtils.LogIn_showad, logInResult.showad)
            SharedPreferencesUtil.saveData(context, AppUtils.LogIn_token, logInResult.token)
            SharedPreferencesUtil.saveData(context, AppUtils.LogIn_ugc, logInResult.ugc)
        }catch (e: Exception){
            Log.i("LogUtil", "error: ${e.toString()}")
        }
    }

}

