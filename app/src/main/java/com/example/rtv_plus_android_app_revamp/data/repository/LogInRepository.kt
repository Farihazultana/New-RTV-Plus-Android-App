package com.example.rtv_plus_android_app_revamp.data.repository

import android.util.Log
import com.example.rtv_plus_android_app_revamp.data.models.logIn.LogInResponse
import com.example.rtv_plus_android_app_revamp.data.services.ApiServices
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import javax.inject.Inject

class LogInRepository @Inject constructor(private val apiServices: ApiServices){
    suspend fun getLogInData(userName: String, password: String, haspin: String): ResultType<LogInResponse> {
        try {
            val response = apiServices.getLogInData(userName,password,haspin)
            Log.i("TAGS", "successful api call: ${response.code()}")
            if (response.isSuccessful) {
                val data = response.body()
                if (data != null) {
                    Log.i("TAGS", "getSeeAllData: $data")
                }
                if (data != null) {
                    return ResultType.Success(data)
                }
            }
            return ResultType.Error(Exception("Failed to fetch See-All data"))
        } catch (e: Exception) {
            return ResultType.Error(e)
        }
    }
}