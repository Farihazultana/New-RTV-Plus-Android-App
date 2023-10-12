package com.rtvplus.data.repository

import android.util.Log
import com.rtvplus.data.models.subscription.SubscriptionResponse
import com.rtvplus.data.services.ApiServices
import com.rtvplus.utils.ResultType
import javax.inject.Inject

class SubscriptionRepository @Inject constructor(private val apiServices: ApiServices) {

    suspend fun getSubscriptionData(msisdn : String): ResultType<SubscriptionResponse> {
        try {
            val response = apiServices.getSubscriptionData(msisdn)
            Log.i("TAGY", "successful api call: ${response.code()}")
            if (response.isSuccessful) {
                val data = response.body()
                if (data != null) {
                    Log.i("TAGY", "getSubscriptionData: ${data.subschemes}")
                }
                if (data != null) {
                    return ResultType.Success(data)
                }
            }
            return ResultType.Error(Exception("Failed to fetch Subscription data"))
        } catch (e: Exception) {
            return ResultType.Error(e)
        }
    }

}