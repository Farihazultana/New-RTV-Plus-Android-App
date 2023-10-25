package com.rtvplus.ui.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rtvplus.data.models.local_payment.LocalPaymentResponse
import com.rtvplus.data.repository.LocalPaymentRepository
import com.rtvplus.utils.ResultType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocalPaymentViewModel @Inject constructor(private val repository: LocalPaymentRepository):ViewModel() {
    private val _localPaymentData = MutableLiveData<ResultType<LocalPaymentResponse>>(ResultType.Loading)
    val localPaymentData : MutableLiveData<ResultType<LocalPaymentResponse>> = _localPaymentData

    fun fetchLocalPaymentData(msisdn: String, d: String){
        viewModelScope.launch {
            try {
                val result = repository.getLocalPaymentData(msisdn,d)
                _localPaymentData.value = result
            }catch (e:Exception){
                _localPaymentData.value = ResultType.Error(e)
            }
        }
    }
}