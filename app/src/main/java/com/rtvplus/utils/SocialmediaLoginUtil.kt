package com.rtvplus.utils

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.rtvplus.ui.viewmodels.GoogleLogInViewModel

class SocialmediaLoginUtil {
    interface ObserverListenerSocial {
        fun observerListenerSocial(result: String)
    }



    private lateinit var observerListenerGoogle: ObserverListenerSocial



    fun fetchGoogleLogInData(viewModelStoreOwner: ViewModelStoreOwner,username: String, firstName: String, lastName: String, email: String, imgUrl: String){
        val googleLogInViewModel = ViewModelProvider(viewModelStoreOwner)[GoogleLogInViewModel::class.java]
        googleLogInViewModel.fetchGoogleLogInData("social", "google", username, "", firstName, lastName,email, imgUrl)
    }

    fun observeGoogleLogInData(context: Context, lifecycleOwner: LifecycleOwner, viewModelStoreOwner: ViewModelStoreOwner, listener : ObserverListenerSocial){
        val googleLogInViewModel = ViewModelProvider(viewModelStoreOwner)[GoogleLogInViewModel::class.java]

        this.observerListenerGoogle = listener
        googleLogInViewModel.googleLogInData.observe(lifecycleOwner) {
            when (it) {
                is ResultType.Success -> {
                    val socialLoginResult = it.data[0]
                    val result = socialLoginResult.result

                    Log.i("OneTap", "observeGoogleLogInData Packcode: ${socialLoginResult.packcode}")

                    this.observerListenerGoogle.observerListenerSocial(result)

                    //Store login data
                    SharedPreferencesUtil.saveLogInData(context,socialLoginResult)
                }

                is ResultType.Error -> {
                    //Toast.makeText(context, "Something is wrong, please try again!", Toast.LENGTH_LONG).show()
                }

                else -> {
                    Log.i(ContentValues.TAG, "onActivityResult: Login data not available")
                }
            }
        }
    }



}