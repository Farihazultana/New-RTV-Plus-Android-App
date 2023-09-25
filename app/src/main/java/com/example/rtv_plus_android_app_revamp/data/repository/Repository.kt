package com.example.rtv_plus_android_app_revamp.data.repository

import android.util.Log
import com.example.rtv_plus_android_app_revamp.data.models.home.HomeResponse
import com.example.rtv_plus_android_app_revamp.data.models.seeAll.SeeAllResponse
import com.example.rtv_plus_android_app_revamp.data.models.subscription.SubscriptionResponse
import com.example.rtv_plus_android_app_revamp.data.services.ApiServices
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import javax.inject.Inject

class Repository @Inject constructor(private val apiServices: ApiServices) {
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

    suspend fun getSeeAllData(page: String, ct: String, tc: String, testval: String): ResultType<SeeAllResponse> {
        try {
            val response = apiServices.getSeeAllData(page,ct,tc,testval)
            Log.i("TAGS", "successful api call: ${response.code()}")
            if (response.isSuccessful) {
                val data = response.body()
                if (data != null) {
                    Log.i("TAGS", "getSeeAllData: ${data.contents}")
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