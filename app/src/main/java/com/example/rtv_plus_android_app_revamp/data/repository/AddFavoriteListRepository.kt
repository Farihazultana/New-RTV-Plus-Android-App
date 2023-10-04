package com.example.rtv_plus_android_app_revamp.data.repository

import android.util.Log
import com.example.rtv_plus_android_app_revamp.data.models.favorite_list.AddListResponse
import com.example.rtv_plus_android_app_revamp.data.models.favorite_list.FavoriteResponse
import com.example.rtv_plus_android_app_revamp.data.services.ApiServices
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import javax.inject.Inject

class AddFavoriteListRepository @Inject constructor(private val apiServices: ApiServices) {
    suspend fun addFavoriteContents(myval: String, username: String): ResultType<AddListResponse> {
        try {
            val response = apiServices.addFavoriteContent(myval, username)
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