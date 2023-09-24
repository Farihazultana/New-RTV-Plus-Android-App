package com.example.rtv_plus_android_app_revamp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rtv_plus_android_app_revamp.data.models.single_content.playlist.PlayListResponse
import com.example.rtv_plus_android_app_revamp.data.models.single_content.single.SingleContentResponse
import com.example.rtv_plus_android_app_revamp.data.repository.PlayListRepository
import com.example.rtv_plus_android_app_revamp.data.repository.SingleContentRepository
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayListViewModel @Inject constructor(private val singleContentRepository: PlayListRepository) : ViewModel() {
    private val _content = MutableLiveData<ResultType<PlayListResponse>>(ResultType.Loading)
    val content: MutableLiveData<ResultType<PlayListResponse>> = _content

    fun fetchPlayListContent(username: String, cc: String, resolution: String) {
        viewModelScope.launch {
            try {
                val result = singleContentRepository.getPlayListData(username, cc, resolution)
                _content.value = result
            } catch (e: Exception) {
                _content.value = ResultType.Error(e)
                Log.e("PlayListViewModel", e.toString())
            }
        }
    }
}