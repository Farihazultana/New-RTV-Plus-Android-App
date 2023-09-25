package com.example.rtv_plus_android_app_revamp.data.repository

import android.util.Log
import com.example.rtv_plus_android_app_revamp.data.models.home.HomeRequest
import com.example.rtv_plus_android_app_revamp.data.models.home.HomeResponse
import com.example.rtv_plus_android_app_revamp.data.services.ApiServices
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import javax.inject.Inject

class HomeRepository @Inject constructor(private val apiServices: ApiServices) {
    suspend fun getHomeData(msisdn: String, view: String): ResultType<HomeResponse> {
        return try {
            val response = apiServices.getHomeData(msisdn, view)
            if (response.isSuccessful) {
                response.body()?.let {
                    ResultType.Success(it)
                } ?: ResultType.Error(Exception("Response body is null"))
            } else {
                ResultType.Error(Exception("Failed to fetch home data"))
            }
        } catch (e: Exception) {
            ResultType.Error(e)
    suspend fun getHomeData(msisdn : String, view: String): ResultType<HomeResponse> {
        try {
            val response = apiServices.getHomeData(msisdn,view)
            if (response.isSuccessful) {
                val data = response.body()
                if (data != null) {
                    return ResultType.Success(data)
                }
            }
            return ResultType.Error(Exception("Failed to fetch home data"))
        } catch (e: Exception) {

            return ResultType.Error(e)
        }
    }
}