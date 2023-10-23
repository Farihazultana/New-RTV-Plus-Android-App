package com.rtvplus.data.repository

import com.rtvplus.data.models.feedback.FeedbackResponse
import com.rtvplus.data.models.post_play_time.PostPlayTimeResponse
import com.rtvplus.data.services.ApiServices
import com.rtvplus.utils.ResultType
import javax.inject.Inject

class SavePlayTimeRepository @Inject constructor(private val apiServices: ApiServices) {
    suspend fun savPlayTime(
        time: String,
        contentid: String,
        username: String,
        playtime: String,
    ): ResultType<PostPlayTimeResponse> {
        try {
            val response = apiServices.savePlayData(time,contentid,username, playtime)
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