package com.rtvplus.utils

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
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

                    this.observerListener.observerListener(result)
                    SharedPreferencesUtil.saveData(context, AppUtils.LogInKey, result)

                    //Store Login Data
                    SharedPreferencesUtil.saveLogInData(context,logInResult)

                }

                is ResultType.Error -> {
                    //Toast.makeText(context, "Something went wrong! Try again.", Toast.LENGTH_SHORT).show()
                }

                else -> {

                }

            }
        }

    }

}