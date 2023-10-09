package com.rtvplus.data.repository

import com.rtvplus.data.models.search.SearchResponse
import com.rtvplus.data.services.ApiServices
import com.rtvplus.utils.ResultType
import javax.inject.Inject

class SearchRepository @Inject constructor(private val apiServices: ApiServices) {
    suspend fun getSearchResultData(fromsrc: String, s: String): ResultType<SearchResponse> {
        return try {
            val response = apiServices.getSearchResultData(fromsrc, s)
            if (response.isSuccessful) {
                response.body()?.let {
                    ResultType.Success(it)
                } ?: ResultType.Error(Exception("Response body is null"))
            } else {
                ResultType.Error(Exception("Failed to fetch home data"))
            }
        } catch (e: Exception) {
            ResultType.Error(e)
        }
    }
}