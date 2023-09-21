package com.example.rtv_plus_android_app_revamp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rtv_plus_android_app_revamp.data.models.home.HomeResponse
import com.example.rtv_plus_android_app_revamp.data.models.seeAll.SeeAllResponse
import com.example.rtv_plus_android_app_revamp.data.models.subscription.SubscriptionResponse
import com.example.rtv_plus_android_app_revamp.data.repository.Repository
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ViewModels @Inject constructor(private val repository: Repository) : ViewModel() {
    //home viewModel
    private val _homeData = MutableStateFlow<ResultType<HomeResponse>>(ResultType.Loading)
    val homeData: StateFlow<ResultType<HomeResponse>> = _homeData
    fun fetchHomeData(msisdn : String , view : String) {
        viewModelScope.launch {
            try {
                val result = repository.getHomeData(msisdn,view)
                _homeData.value = result
            } catch (e: Exception) {
                _homeData.value = ResultType.Error(e)
            }
        }
    }

    //subscription viewModel
    private val _subscriptionData = MutableStateFlow<ResultType<SubscriptionResponse>>(ResultType.Loading)
    val subscriptionData : StateFlow<ResultType<SubscriptionResponse>> = _subscriptionData
    fun fetchSubscriptionData(msisdn : String){
        viewModelScope.launch {
            try {
                val result = repository.getSubscriptionData(msisdn)
                _subscriptionData.value = result
            } catch (e: Exception) {
                _subscriptionData.value = ResultType.Error(e)
            }
        }
    }

    //see-all viewModel
    private val _seeAllData = MutableStateFlow<ResultType<SeeAllResponse>>(ResultType.Loading)
    val seeAllData : StateFlow<ResultType<SeeAllResponse>> = _seeAllData
    fun fetchSeeAllData(page: String, ct: String, tc: String, testval: String){
        viewModelScope.launch {
            try {
                val result = repository.getSeeAllData(page,ct,tc,testval)
                _seeAllData.value=result
            } catch (e: Exception) {
                _seeAllData.value = ResultType.Error(e)
            }
        }
    }

}