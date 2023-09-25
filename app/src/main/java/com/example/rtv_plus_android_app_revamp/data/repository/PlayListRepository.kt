package com.example.rtv_plus_android_app_revamp.data.repository

import android.util.Log
import com.example.rtv_plus_android_app_revamp.data.models.single_content.playlist.PlayListResponse
import com.example.rtv_plus_android_app_revamp.data.models.single_content.single.SingleContentResponse
import com.example.rtv_plus_android_app_revamp.data.services.ApiServices
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import javax.inject.Inject

class PlayListRepository @Inject constructor(private val apiServices: ApiServices) {
    suspend fun getPlayListData(username: String, cc: String, resolution: String): ResultType<PlayListResponse> {
        return try {
            val response = apiServices.getPlayListData(username, cc, resolution)
            Log.e("eeeeeeeeeeeeeeeeeeeeeeee",response.body().toString())
            if (response.isSuccessful) {
                response.body()?.let {
                    ResultType.Success(it)
                } ?: ResultType.Error(Exception("Response body is null"))
            } else {
                ResultType.Error(Exception("Failed to fetch single content data"))
            }
        } catch (e: Exception) {
            Log.e("eeeeeeeeeeeeeeeeeeeeeeee",e.toString())
            ResultType.Error(e)
        }
    }
}