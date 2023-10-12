package com.rtvplus

import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.rtvplus.data.models.device_info.DeviceInfo
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

        collectDeviceAndDeviceInfo()
    }

    private fun collectDeviceAndDeviceInfo() {
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