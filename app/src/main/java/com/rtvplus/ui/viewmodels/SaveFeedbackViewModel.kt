package com.rtvplus.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rtvplus.data.models.feedback.FeedbackResponse
import com.rtvplus.data.repository.SaveFeedbackRepository
import com.rtvplus.utils.ResultType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SaveFeedbackViewModel @Inject constructor(private val saveFeedbackRepository: SaveFeedbackRepository) :
    ViewModel() {
    private val _saveFeedbackResponse =
        MutableLiveData<ResultType<FeedbackResponse>>(ResultType.Loading)
    val saveFeedbackResponse: LiveData<ResultType<FeedbackResponse>> = _saveFeedbackResponse
    fun saveFeedback(username: String, comment: String) {
        viewModelScope.launch {
            try {
                val result = saveFeedbackRepository.saveFeedback(username, comment)
                _saveFeedbackResponse.value = result
            } catch (e: Exception) {
                _saveFeedbackResponse.value = ResultType.Error(e)
            }
        }
    }
}