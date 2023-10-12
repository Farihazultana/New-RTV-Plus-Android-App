package com.rtvplus.data.repository

import com.rtvplus.data.models.single_content.single.SingleContentResponse
import com.rtvplus.data.services.ApiServices
import com.rtvplus.utils.ResultType
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