package com.example.rtv_plus_android_app_revamp.data.repository

import android.util.Log
import com.example.rtv_plus_android_app_revamp.data.models.socialmedia_login.google.GoogleLogInResponse
import com.example.rtv_plus_android_app_revamp.data.services.ApiServices
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import java.lang.Exception
import javax.inject.Inject

class GoogleLogInRepository @Inject constructor(private val apiServices: ApiServices) {
    suspend fun getGoogleLogInData(
        logintype: String,
        source: String,
        username: String,
        password: String,
        firstName: String,
        lastName: String,
        email: String,
        imgUrl: String
    ): ResultType<GoogleLogInResponse> {
        try {
            val response = apiServices.getGoogleLogInData(
                logintype,
                source,
                username,
                password,
                firstName,
                lastName,
                email,
                imgUrl
            )
            Log.i("OneTap", "successful api call: ${response.code()}")
            if (response.isSuccessful) {
                val data = response.body()
                if (data != null) {
                    Log.i("OneTap", "getGoogleLogInData: $data")
                }
                if (data != null) {
                    return ResultType.Success(data)
                }
            }
            return ResultType.Error(Exception("Failed to fetch Google LogIn data"))
        } catch (e: Exception) {
            return ResultType.Error(e)
        }
    }
}