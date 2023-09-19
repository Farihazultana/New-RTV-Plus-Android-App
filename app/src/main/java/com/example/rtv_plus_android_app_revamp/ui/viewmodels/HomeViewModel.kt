package com.example.rtv_plus_android_app_revamp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rtv_plus_android_app_revamp.data.models.home.HomeRequest
import com.example.rtv_plus_android_app_revamp.data.models.home.HomeResponse
import com.example.rtv_plus_android_app_revamp.data.repository.HomeRepository
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val homeRepository: HomeRepository) : ViewModel() {
    private val _homeData = MutableStateFlow<ResultType<HomeResponse>>(ResultType.Loading)
    val homeData: StateFlow<ResultType<HomeResponse>> = _homeData

    fun fetchHomeData(msisdn : String , view : String) {
        viewModelScope.launch {
            try {
                val result = homeRepository.getHomeData(msisdn,view)
                _homeData.value = result
            } catch (e: Exception) {
                _homeData.value = ResultType.Error(e)
            }
        }
    }
}