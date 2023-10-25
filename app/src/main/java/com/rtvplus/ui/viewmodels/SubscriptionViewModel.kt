package com.rtvplus.ui.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rtvplus.data.models.subscription.SubscriptionResponse
import com.rtvplus.data.repository.SubscriptionRepository
import com.rtvplus.utils.ResultType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SubscriptionViewModel @Inject constructor(private val subscriptionRepository: SubscriptionRepository) :
    ViewModel() {

    //subscription viewModel
    private val _subscriptionData =
        MutableLiveData<ResultType<SubscriptionResponse>>(ResultType.Loading)
    val subscriptionData: MutableLiveData<ResultType<SubscriptionResponse>> = _subscriptionData
    fun fetchSubscriptionData(msisdn: String) {
        viewModelScope.launch {
            try {
                val result = subscriptionRepository.getSubscriptionData(msisdn)
                Log.i("TAGY", "fetchSubscriptionData: $result")
                _subscriptionData.value = result
            } catch (e: Exception) {
                _subscriptionData.value = ResultType.Error(e)
            }
        }
    }

}