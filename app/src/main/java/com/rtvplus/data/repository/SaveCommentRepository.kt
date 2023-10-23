package com.rtvplus.data.repository

import com.rtvplus.data.models.comment.CommentResponse
import com.rtvplus.data.services.ApiServices
import com.rtvplus.utils.ResultType
import javax.inject.Inject

class SaveCommentRepository @Inject constructor(private val apiServices: ApiServices) {
    suspend fun saveComment(
        username: String,
        comment: String
    ): ResultType<CommentResponse> {
        try {
            val response = apiServices.saveComment(username, comment)
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