package com.rtvplus.data.repository

import com.rtvplus.data.models.home.HomeResponse
import com.rtvplus.data.services.ApiServices
import com.rtvplus.utils.ResultType
import javax.inject.Inject

class HomeRepository @Inject constructor(private val apiServices: ApiServices) {
    suspend fun getHomeData(msisdn : String, view: String, version: String, fromsrc: String, lng: String ): ResultType<HomeResponse> {
        try {
            val response = apiServices.getHomeData(msisdn,view,version,fromsrc,lng)
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