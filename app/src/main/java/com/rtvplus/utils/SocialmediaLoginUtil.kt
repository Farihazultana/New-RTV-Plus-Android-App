package com.rtvplus.utils

import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.rtvplus.ui.viewmodels.SocialLogInViewModel

class SocialmediaLoginUtil {
    interface ObserverListenerSocial {
        fun observerListenerSocial(result: String, loginSrc: String)
    }

    private lateinit var observerListenerGoogle: ObserverListenerSocial

    fun fetchSocialLogInData(viewModelStoreOwner: ViewModelStoreOwner, loginSrc: String, username: String, firstName: String, lastName: String, email: String, imgUrl: String){
        val googleLogInViewModel = ViewModelProvider(viewModelStoreOwner)[SocialLogInViewModel::class.java]
        googleLogInViewModel.fetchSocialLogInData("social", loginSrc, username, "", firstName, lastName,email, imgUrl)
    }

    fun observeSocialLogInData(context: Context, lifecycleOwner: LifecycleOwner, viewModelStoreOwner: ViewModelStoreOwner, listener : ObserverListenerSocial){
        val googleLogInViewModel = ViewModelProvider(viewModelStoreOwner)[SocialLogInViewModel::class.java]

        this.observerListenerGoogle = listener
        googleLogInViewModel.googleLogInData.observe(lifecycleOwner) {
            when (it) {
                is ResultType.Success -> {
                    val socialLoginResult = it.data[0]
                    val result = socialLoginResult.result
                    val loginSrc = socialLoginResult.loginsrc.toString()


                    this.observerListenerGoogle.observerListenerSocial(result, loginSrc)

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