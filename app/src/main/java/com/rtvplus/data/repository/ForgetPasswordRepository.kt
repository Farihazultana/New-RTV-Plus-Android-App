package com.rtvplus.data.repository

import android.util.Log
import com.rtvplus.data.models.forgetPassword.ForgetPasswordResponse
import com.rtvplus.data.services.ApiServices
import com.rtvplus.utils.ResultType
import java.lang.Exception
import javax.inject.Inject

class ForgetPasswordRepository @Inject constructor(private val apiServices: ApiServices) {
    suspend fun getForgetPasswordData(
        msisdn: String,
        forget: String,
    ): ResultType<ForgetPasswordResponse> {
        try {
            val response = apiServices.getForgetPasswordData(msisdn, forget)
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