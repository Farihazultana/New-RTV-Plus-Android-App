package com.example.rtv_plus_android_app_revamp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rtv_plus_android_app_revamp.data.models.info.InfoResponse
import com.example.rtv_plus_android_app_revamp.data.repository.InfoRepository
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InfoViewModel @Inject constructor(private val infoRepository: InfoRepository) :
    ViewModel() {
    private val _info = MutableLiveData<ResultType<InfoResponse>>(ResultType.Loading)
    val info: LiveData<ResultType<InfoResponse>> = _info
    fun fetchInfo(msisdn: String, appinfo: String) {
        viewModelScope.launch {
            try {
                val result = infoRepository.getInfo(msisdn, appinfo)
                _info.value = result
            } catch (e: Exception) {
                _info.value = ResultType.Error(e)
            }
        }
    }
}