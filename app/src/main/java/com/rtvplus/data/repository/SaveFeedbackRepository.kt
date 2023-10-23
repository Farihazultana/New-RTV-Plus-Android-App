package com.rtvplus.data.repository

import com.rtvplus.data.models.feedback.FeedbackResponse
import com.rtvplus.data.services.ApiServices
import com.rtvplus.utils.ResultType
import javax.inject.Inject

class SaveFeedbackRepository @Inject constructor(private val apiServices: ApiServices) {
    suspend fun saveFeedback(
        username: String,
        comment: String
    ): ResultType<FeedbackResponse> {
        try {
            val response = apiServices.saveFeedback(username, comment)
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