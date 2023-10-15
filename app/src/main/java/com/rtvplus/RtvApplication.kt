package com.rtvplus

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.rtvplus.data.models.device_info.DeviceInfo
import com.rtvplus.utils.AppUtils
import com.rtvplus.utils.AppUtils.emailRegex
import com.rtvplus.utils.AppUtils.phoneRegex
import com.rtvplus.utils.SharedPreferencesUtil
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class RtvApplication : Application() {

    @Inject
    lateinit var deviceInfo: DeviceInfo

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    override fun onCreate() {
        super.onCreate()
        firebaseAnalytics = Firebase.analytics

        collectDeviceAndAppInfo()

    }

    private fun collectDeviceAndAppInfo() {

        val username = SharedPreferencesUtil.getData(this, AppUtils.UsernameInputKey, "").toString()
        val isEmail = emailRegex.matches(username)
        val isPhoneNumber = phoneRegex.matches(username)

        Log.e("iiiiiiiiiiiiiiiiiiiiii", "phone: ${username}")

        if (isPhoneNumber) {
            Log.e("iiiiiiiiiiiiiiiiiiiiii", "phone: ${username}")
            val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            //  val simSerialNumber = telephonyManager.simSerialNumber
            val operatorName = telephonyManager.simOperatorName
            val operatorCode = telephonyManager.simOperator
            //val simCountryIso = telephonyManager.simCountryIso
            //  deviceInfo.simSerialNumber = simSerialNumber
            deviceInfo.operatorName = operatorName
            deviceInfo.operator = operatorCode

        }
        deviceInfo.deviceId = Build.ID
        deviceInfo.softwareVersion = Build.VERSION.RELEASE
        deviceInfo.brand = Build.BRAND
        deviceInfo.model = Build.MODEL
        deviceInfo.release = Build.VERSION.RELEASE
        deviceInfo.sdkVersion = Build.VERSION.SDK_INT
        deviceInfo.versionCode = try {
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pInfo.longVersionCode.toInt()
            } else {
                @Suppress("DEPRECATION") pInfo.versionCode
            }
        } catch (e: PackageManager.NameNotFoundException) {
            -1
        }
    }

}