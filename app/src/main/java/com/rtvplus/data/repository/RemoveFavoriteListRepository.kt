package com.rtvplus.data.repository

import com.rtvplus.data.models.favorite_list.RemoveListResponse
import com.rtvplus.data.services.ApiServices
import com.rtvplus.utils.ResultType
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