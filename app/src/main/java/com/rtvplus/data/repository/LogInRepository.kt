package com.rtvplus.data.repository

import android.util.Log
import com.rtvplus.data.models.logIn.LogInResponse
import com.rtvplus.data.services.ApiServices
import com.rtvplus.utils.ResultType
import javax.inject.Inject

class LogInRepository @Inject constructor(private val apiServices: ApiServices){
    suspend fun getLogInData(userName: String, password: String, haspin: String, tc:String): ResultType<LogInResponse> {
        try {
            val response = apiServices.getLogInData(userName,password,haspin, tc)
            Log.i("TAGS", "successful api call: ${response.code()}")
            if (response.isSuccessful) {
                val data = response.body()
                if (data != null) {
                    Log.i("TAGS", "getLogInData: $data")
                }
                if (data != null) {
                    return ResultType.Success(data)
                }
            }
            return ResultType.Error(Exception("Failed to fetch Login data"))
        } catch (e: Exception) {
            return ResultType.Error(e)
        }
    }
}