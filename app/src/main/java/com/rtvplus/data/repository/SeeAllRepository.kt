package com.rtvplus.data.repository

import android.util.Log
import com.rtvplus.data.models.seeAll.SeeAllResponse
import com.rtvplus.data.services.ApiServices
import com.rtvplus.utils.ResultType
import javax.inject.Inject

class SeeAllRepository @Inject constructor(private val apiServices: ApiServices) {
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