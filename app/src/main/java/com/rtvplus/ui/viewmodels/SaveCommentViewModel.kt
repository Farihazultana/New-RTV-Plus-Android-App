package com.rtvplus.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rtvplus.data.models.comment.CommentResponse
import com.rtvplus.data.repository.SaveCommentRepository
import com.rtvplus.utils.ResultType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SaveCommentViewModel @Inject constructor(private val saveCommentRepository: SaveCommentRepository) :
    ViewModel() {
    private val _saveCommentResponse =
        MutableLiveData<ResultType<CommentResponse>>(ResultType.Loading)
    val saveCommentResponse: LiveData<ResultType<CommentResponse>> = _saveCommentResponse
    fun saveComment(username: String, comment: String) {
        viewModelScope.launch {
            try {
                val result = saveCommentRepository.saveComment(username, comment)
                _saveCommentResponse.value = result
            } catch (e: Exception) {
                _saveCommentResponse.value = ResultType.Error(e)
            }
        }
    }
}