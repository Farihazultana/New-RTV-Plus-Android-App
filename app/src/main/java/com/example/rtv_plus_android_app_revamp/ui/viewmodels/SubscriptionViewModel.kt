package com.example.rtv_plus_android_app_revamp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rtv_plus_android_app_revamp.data.models.subscription.SubscriptionResponse
import com.example.rtv_plus_android_app_revamp.data.repository.SubscriptionRepository
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import kotlinx.coroutines.launch
import javax.inject.Inject

class SubscriptionViewModel @Inject constructor(private val subscriptionRepository: SubscriptionRepository) : ViewModel(){
    private val _subscriptionData = MutableLiveData<ResultType<SubscriptionResponse>>(ResultType.Loading)
    val subscriptionData : LiveData<ResultType<SubscriptionResponse>> = _subscriptionData

    fun fetchSubscriptionData(msisdn : String){
        viewModelScope.launch {
            try {
                val result = subscriptionRepository.getSubscriptionData(msisdn)
                _subscriptionData.value = result
            } catch (e: Exception) {
                _subscriptionData.value = ResultType.Error(e)
            }
        }
    }
}