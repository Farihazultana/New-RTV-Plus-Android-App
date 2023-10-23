package com.rtvplus.ui.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rtvplus.data.models.single_content.playlist.PlayListResponse
import com.rtvplus.data.repository.PlayListRepository
import com.rtvplus.utils.ResultType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayListViewModel @Inject constructor(private val singleContentRepository: PlayListRepository) : ViewModel() {
    private val _content = MutableLiveData<ResultType<PlayListResponse>>(ResultType.Loading)
    val content: MutableLiveData<ResultType<PlayListResponse>> = _content

    fun fetchPlayListContent(username: String, cc: String, resolution: String, ct: String) {
        viewModelScope.launch {
            try {
                val result = singleContentRepository.getPlayListData(username, cc, resolution, ct)
                _content.value = result
            } catch (e: Exception) {
                _content.value = ResultType.Error(e)
                Log.e("PlayListViewModel", e.toString())
            }
        }
    }
}