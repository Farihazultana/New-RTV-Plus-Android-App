package com.example.rtv_plus_android_app_revamp.data.repository

import android.util.Log
import com.example.rtv_plus_android_app_revamp.data.models.forgetPassword.ForgetPasswordResponse
import com.example.rtv_plus_android_app_revamp.data.services.ApiServices
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import java.lang.Exception
import javax.inject.Inject

class ForgetPasswordRepository @Inject constructor(private val apiServices: ApiServices) {
    suspend fun getForgetPasswordData(
        username: String,
        password: String,
        newPass: String
    ): ResultType<ForgetPasswordResponse> {
        try {
            val response = apiServices.getForgetPasswordData(username, password, newPass)
            Log.i("Forget", "successful api call: ${response.code()}")
            if (response.isSuccessful) {
                val data = response.body()
                if (data != null) {
                    Log.i("Forget", "getForgetPasswordData: $data")
                }
                if (data != null) {
                    return ResultType.Success(data)
                }
            }

            return ResultType.Error(Exception("Failed to fetch Forget Password data"))

        } catch (e: Exception) {
            return ResultType.Error(e)
        }
    }
}