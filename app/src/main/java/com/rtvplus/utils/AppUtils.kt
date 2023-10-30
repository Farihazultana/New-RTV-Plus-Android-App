package com.rtvplus.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.rtvplus.R

object AppUtils {
    const val BASE_URL = "https://api-v200.rtvplus.tv/"
    const val PACKAGE_NAME = "com.rtvplus"
    const val PhoneInputKey = "phone_input_key_rtv"
    //const val GoogleSignInKey = "google_key"
    // const val LogInKey = "login_key_key_rtv"

    const val LogInKey = "LogIn_Result"
    const val UsernameInputKey = "User"
    const val UserPasswordKey = "Password"
    const val GoogleSignInKey = "Google"
    const val USER_GMAIL = "rtv_user_gmail_001"
    const val LogInModule = "result"
    const val SignInType = "SignedInWith"
    const val GoogleSignIn_IdToken = "ID token"
    const val GoogleSignIn_FirstName = "firstname"
    const val GoogleSignIn_LastName = "lastname"
    const val GoogleSignIn_ImgUri = "ImageUri"
    const val GoogleSignIn_Email = "Email"

    //Phone Login
    const val LogIn_audioad = "LogIn_audioad"
    const val LogIn_autorenew = "LogIn_autorenew"
    const val LogIn_concurrent = "LogIn_concurrent"
    const val LogIn_concurrenttext = "LogIn_concurrenttext"
    const val LogIn_consent = "LogIn_consent"
    const val LogIn_consenttext = "LogIn_consenttext"
    const val LogIn_consenturl = "LogIn_consenturl"
    const val LogIn_currentversion = "LogIn_currentversion"
    const val LogIn_currentversionios = "LogIn_currentversionios"
    const val LogIn_email = "LogIn_email"
    const val LogIn_enforce = "LogIn_enforce"
    const val LogIn_enforcetext = "LogIn_enforcetext"
    const val LogIn_extrainfo = "LogIn_extrainfo"
    const val LogIn_fullname = "LogIn_fullname"
    const val LogIn_liveurl = "LogIn_liveurl"
    const val LogIn_msisdn = "LogIn_msisdn"
    const val LogIn_packcode = "LogIn_packcode"
    const val LogIn_packname = "LogIn_packname"
    const val LogIn_packtext = "LogIn_packtext"
    const val LogIn_play = "LogIn_play"
    const val LogIn_referral = "LogIn_referral"
    const val LogIn_referralimage = "LogIn_referralimage"
    const val LogIn_result = "LogIn_result"
    const val LogIn_showad = "LogIn_showad"
    const val LogIn_token = "LogIn_token"
    const val LogIn_ugc = "LogIn_ugc"


    //Google login
    const val SocialLogIn_audioad = "SocialLogIn_audioad"
    const val SocialLogIn_concurrent = "SocialLogIn_concurrent"
    const val SocialLogIn_concurrenttext = "SocialLogIn_concurrenttext"
    const val SocialLogIn_consent = "SocialLogIn_consent"
    const val SocialLogIn_consenttext = "SocialLogIn_consenttext"
    const val SocialLogIn_consenturl = "SocialLogIn_consenturl"
    const val SocialLogIn_currentversion = "SocialLogIn_currentversion"
    const val SocialLogIn_currentversionios = "SocialLogIn_currentversionios"
    const val SocialLogIn_email = "SocialLogIn_email"
    const val SocialLogIn_enforce = "SocialLogIn_enforce"
    const val SocialLogIn_enforcetext = "SocialLogIn_enforcetext"
    const val SocialLogIn_extrainfo = "SocialLogIn_extrainfo"
    const val SocialLogIn_fullname = "SocialLogIn_fullname"
    const val SocialLogIn_liveurl = "SocialLogIn_liveurl"
    const val SocialLogIn_loginsrc = "loginsrc"
    const val SocialLogIn_msisdn = "SocialLogIn_msisdn"
    const val SocialLogIn_packcode = "SocialLogIn_packcode"
    const val SocialLogIn_packname = "SocialLogIn_packname"
    const val SocialLogIn_packtext = "SocialLogIn_packtext"
    const val SocialLogIn_play = "SocialLogIn_play"
    const val SocialLogIn_referral = "SocialLogIn_referral"
    const val SocialLogIn_referralimage = "SocialLogIn_referralimage"
    const val SocialLogIn_response = "response"
    const val SocialLogIn_result = "SocialLogIn_result"
    const val SocialLogIn_showad = "SocialLogIn_showad"
    const val SocialLogIn_token = "SocialLogIn_token"


    val emailRegex = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
    val phoneRegex = Regex("^8801[3-9]\\d{8}$")

    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

    @SuppressLint("MissingInflatedId")
    fun showAlertDialog(context: Context) {
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.custom_dialougue_no_internet, null)
        val dialogOkButton = dialogView.findViewById<Button>(R.id.dialog_ok_button)
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setView(dialogView)
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
        dialogOkButton.setOnClickListener {
            alertDialog.dismiss()
        }
    }


}