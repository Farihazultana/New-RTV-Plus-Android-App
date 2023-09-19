package com.example.rtv_plus_android_app_revamp.data.repository

import com.example.rtv_plus_android_app_revamp.data.models.subscription.SubscriptionResponse
import com.example.rtv_plus_android_app_revamp.data.services.ApiServices
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import javax.inject.Inject

class SubscriptionRepository @Inject constructor(private val apiServices: ApiServices) {
    suspend fun getSubscriptionData(msisdn : String): ResultType<SubscriptionResponse> {
        try {
            val response = apiServices.getSubscriptionData(msisdn)
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