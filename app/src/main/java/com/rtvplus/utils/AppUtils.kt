package com.rtvplus.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.rtvplus.R
import com.rtvplus.ui.viewmodels.LogInViewModel
import kotlinx.coroutines.CompletableDeferred

object AppUtils {
    const val BASE_URL = "https://api-v200.rtvplus.tv/"
    const val PACKAGE_NAME = "com.rtvplus"

    const val LogInKey = "LogIn_Result"
    const val UsernameInputKey = "User"

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