package com.rtvplus.data.repository

import com.rtvplus.data.models.single_content.playlist.PlayListResponse
import com.rtvplus.data.services.ApiServices
import com.rtvplus.utils.ResultType
import javax.inject.Inject

class PlayListRepository @Inject constructor(private val apiServices: ApiServices) {
    suspend fun getPlayListData(
        username: String,
        cc: String,
        resolution: String,
        ct: String
    ): ResultType<PlayListResponse> {
        return try {
            val response = apiServices.getPlayListData(username, cc, resolution, ct)
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