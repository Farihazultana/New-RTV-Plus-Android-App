package com.rtvplus

import android.app.Application
import androidx.fragment.app.viewModels
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.rtvplus.ui.viewmodels.SubscriptionViewModel
import dagger.hilt.android.HiltAndroidApp
import androidx.fragment.app.viewModels


@HiltAndroidApp
class RtvApplication : Application() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    override fun onCreate() {
        super.onCreate()
        firebaseAnalytics = Firebase.analytics

    }

}