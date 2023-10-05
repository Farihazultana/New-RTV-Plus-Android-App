package com.example.rtv_plus_android_app_revamp.data.repository

import com.example.rtv_plus_android_app_revamp.data.models.favorite_list.RemoveListResponse
import com.example.rtv_plus_android_app_revamp.data.services.ApiServices
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import javax.inject.Inject

class RemoveFavoriteListRepository @Inject constructor(private val apiServices: ApiServices) {
    suspend fun removeFavoriteContents(
        myval: String,
        username: String
    ): ResultType<RemoveListResponse> {
        try {
            val response = apiServices.removeFavoriteContent(myval, username)
            if (response.isSuccessful) {
                val data = response.body()
                if (data != null) {
                    return ResultType.Success(data)
                }
            }
            return ResultType.Error(Exception("Failed to remove data"))
        } catch (e: Exception) {
            return ResultType.Error(e)
        }
    }
}