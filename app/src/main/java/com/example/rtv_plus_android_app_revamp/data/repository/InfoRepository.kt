package com.example.rtv_plus_android_app_revamp.data.repository

import android.util.Log
import com.example.rtv_plus_android_app_revamp.data.models.info.InfoResponse
import com.example.rtv_plus_android_app_revamp.data.services.ApiServices
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

class InfoRepository @Inject constructor(private val apiServices: ApiServices) {
    suspend fun getInfo(msisdn: String, appinfo: String): ResultType<InfoResponse> {
        try {
            val response = apiServices.getInfoData(msisdn, appinfo)
            if (response.isSuccessful) {
                val data = response.body()
                if (data != null) {
                    return ResultType.Success(data)
                }
            }
            return ResultType.Error(Exception("Failed to fetch home data"))
        } catch (e: Exception) {
            Log.e("xxxxxxxxxxxxxxxxxxxxxxxxxxx", e.toString())
            return ResultType.Error(e)
        }
    }
}