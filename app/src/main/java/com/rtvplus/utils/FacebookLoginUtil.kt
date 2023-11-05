package com.rtvplus.utils

import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.rtvplus.ui.viewmodels.GoogleLogInViewModel

class FacebookLoginUtil {
   /* interface ObserverListenerFacebook {
        fun observerListenerFB(result: String)
    }

    private lateinit var observerListenerFacebook: ObserverListenerFacebook*/

    fun fetchFacebookLogInData(viewModelStoreOwner: ViewModelStoreOwner, username: String, firstName: String, imgUrl: String){
        val fbLoginViewModel = ViewModelProvider(viewModelStoreOwner)[GoogleLogInViewModel::class.java]
        fbLoginViewModel.fetchGoogleLogInData("social","facebook", username, "", firstName,"", "", imgUrl)
    }

    fun observeFacebookLoginData(context: Context, lifecycleOwner: LifecycleOwner, viewModelStoreOwner: ViewModelStoreOwner){
        val fbLogInViewModel = ViewModelProvider(viewModelStoreOwner)[GoogleLogInViewModel::class.java]
        //this.observerListenerFacebook = listener
        fbLogInViewModel.googleLogInData.observe(lifecycleOwner){
            when(it){
                is ResultType.Success -> {
                    val fbLoginResult = it.data[0]
                    val result = fbLoginResult.result
                    Log.i("FacebookProfile", "observeFacebookLoginData: ${fbLoginResult.loginsrc}")
                    //this.observerListenerFacebook.observerListenerFB(result)

                    //store fb login data
                    SharedPreferencesUtil.saveLogInData(context, fbLoginResult)
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