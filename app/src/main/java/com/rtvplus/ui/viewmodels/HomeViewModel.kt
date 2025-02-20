package com.rtvplus.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rtvplus.data.models.home.HomeResponse
import com.rtvplus.data.repository.HomeRepository
import com.rtvplus.utils.ResultType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val homeRepository: HomeRepository) : ViewModel() {
    private val _homeData = MutableLiveData<ResultType<HomeResponse>>(ResultType.Loading)
    val homeData: LiveData<ResultType<HomeResponse>> = _homeData

    fun fetchHomeData(msisdn: String, view: String, version: String, fromsrc: String, lng: String) {
        viewModelScope.launch {
            try {
                val result = homeRepository.getHomeData(msisdn, view, version, fromsrc, lng)
                _homeData.value = result
            } catch (e: Exception) {
                _homeData.value = ResultType.Error(e)
                Log.e("HomeViewModel", e.toString())
            }
        }
    }
}