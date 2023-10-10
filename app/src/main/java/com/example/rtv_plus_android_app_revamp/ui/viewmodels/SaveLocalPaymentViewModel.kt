package com.example.rtv_plus_android_app_revamp.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rtv_plus_android_app_revamp.data.models.local_payment.SaveLocalPaymentResponse
import com.example.rtv_plus_android_app_revamp.data.repository.SaveLocalPaymentRepository
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SaveLocalPaymentViewModel @Inject constructor(private val repository: SaveLocalPaymentRepository): ViewModel(){
    private val _saveLocalPaymentData = MutableLiveData<ResultType<SaveLocalPaymentResponse>>(ResultType.Loading)
    val saveLocalPaymentData : MutableLiveData<ResultType<SaveLocalPaymentResponse>> = _saveLocalPaymentData

    fun fetchSavedLocalPaymentData(msisdn : String, paymentId: String, orderid: String){
        viewModelScope.launch {
            try {
                val result = repository.getSavedLocalPaymentData(msisdn, paymentId, orderid)
                _saveLocalPaymentData.value = result
            }catch (e: Exception){
                _saveLocalPaymentData.value = ResultType.Error(e)
            }
        }
    }
}