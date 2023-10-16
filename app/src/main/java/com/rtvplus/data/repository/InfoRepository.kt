package com.rtvplus.data.repository

import android.util.Log
import com.rtvplus.data.models.info.InfoResponse
import com.rtvplus.data.services.ApiServices
import com.rtvplus.utils.ResultType
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
            return ResultType.Error(e)
        }
    }
}