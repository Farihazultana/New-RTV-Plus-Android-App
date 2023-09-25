package com.example.rtv_plus_android_app_revamp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rtv_plus_android_app_revamp.data.models.single_content.single.SingleContentResponse
import com.example.rtv_plus_android_app_revamp.data.repository.SingleContentRepository
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SingleContentViewModel @Inject constructor(private val singleContentRepository: SingleContentRepository) : ViewModel() {
    private val _content = MutableLiveData<ResultType<SingleContentResponse>>(ResultType.Loading)
    val content: MutableLiveData<ResultType<SingleContentResponse>> = _content

    fun fetchSingleContent(msisdn: String, cc: String, fromsrc: String) {
        viewModelScope.launch {
            try {
                val result = singleContentRepository.getSingleData(msisdn, cc, fromsrc)
                _content.value = result
            } catch (e: Exception) {
                _content.value = ResultType.Error(e)
                Log.e("SingleContentViewModel", e.toString())
            }
        }
    }
}