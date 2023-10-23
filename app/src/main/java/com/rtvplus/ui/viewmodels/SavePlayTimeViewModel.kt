package com.rtvplus.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rtvplus.data.models.post_play_time.PostPlayTimeResponse
import com.rtvplus.data.repository.SavePlayTimeRepository
import com.rtvplus.utils.ResultType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavePlayTimeViewModel @Inject constructor(private val savePlayTimeRepository: SavePlayTimeRepository) :
    ViewModel() {
    private val _savePlayTimeResponse =
        MutableLiveData<ResultType<PostPlayTimeResponse>>(ResultType.Loading)
    val savePlayTimeResponse: LiveData<ResultType<PostPlayTimeResponse>> = _savePlayTimeResponse
    fun savePlayTime(time: String, contentid: String, username: String, playtime: String) {
        viewModelScope.launch {
            try {
                val result = savePlayTimeRepository.savPlayTime(time, contentid, username, playtime)
                _savePlayTimeResponse.value = result
            } catch (e: Exception) {
                _savePlayTimeResponse.value = ResultType.Error(e)
            }
        }
    }
}