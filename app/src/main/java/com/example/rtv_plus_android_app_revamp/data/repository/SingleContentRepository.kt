package com.example.rtv_plus_android_app_revamp.data.repository

import android.util.Log
import com.example.rtv_plus_android_app_revamp.data.models.home.HomeResponse
import com.example.rtv_plus_android_app_revamp.data.models.single_content.single.SingleContentResponse
import com.example.rtv_plus_android_app_revamp.data.services.ApiServices
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import javax.inject.Inject

class SingleContentRepository @Inject constructor(private val apiServices: ApiServices) {
    suspend fun getSingleData(msisdn: String, cc: String, fromsrc: String): ResultType<SingleContentResponse> {
        return try {
            val response = apiServices.getSingleData(msisdn, cc, fromsrc)
            if (response.isSuccessful) {
                response.body()?.let {
                    ResultType.Success(it)
                } ?: ResultType.Error(Exception("Response body is null"))
            } else {
                ResultType.Error(Exception("Failed to fetch single content data"))
            }
        } catch (e: Exception) {
            ResultType.Error(e)
        }
    }
}