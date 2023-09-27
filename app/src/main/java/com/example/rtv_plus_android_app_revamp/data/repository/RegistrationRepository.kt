package com.example.rtv_plus_android_app_revamp.data.repository

import android.util.Log
import com.example.rtv_plus_android_app_revamp.data.models.registration.RegistrationResponse
import com.example.rtv_plus_android_app_revamp.data.services.ApiServices
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import javax.inject.Inject
import kotlin.Exception

class RegistrationRepository @Inject constructor(private val apiServices: ApiServices) {
    suspend fun getRegistrationData(msisdn: String): ResultType<RegistrationResponse> {
        try {
            val response = apiServices.getRegistrationData(msisdn)
            Log.i("TAGS", "successful api call: ${response.code()}")
            if (response.isSuccessful) {
                val data = response.body()
                if (data != null) {
                    Log.i("TAGS", "getRegistrationData: $data")
                    return ResultType.Success(data)
                }
            }
            return  ResultType.Error(Exception("Failed to fetch Registration Data"))
        } catch (e: Exception) {
            return ResultType.Error(e)
        }
    }
}