package com.example.rtv_plus_android_app_revamp.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rtv_plus_android_app_revamp.data.models.coupon_payment.RedeemCouponPaymentResponse
import com.example.rtv_plus_android_app_revamp.data.repository.RedeemCouponRepository
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RedeemCouponViewModel @Inject constructor(private val repository: RedeemCouponRepository): ViewModel(){
    private val _redeemCouponPayemntData = MutableLiveData<ResultType<RedeemCouponPaymentResponse>>(ResultType.Loading)
    val redeemCuoponPaymentData: MutableLiveData<ResultType<RedeemCouponPaymentResponse>> = _redeemCouponPayemntData

    fun fetchRedeemCouponPaymentData(msisdn: String, couponcode: String){
        viewModelScope.launch {
            try {
                val result = repository.getRedeemCouponPaymentData(msisdn, couponcode)
                _redeemCouponPayemntData.value = result
            }catch (e: Exception){
                _redeemCouponPayemntData.value = ResultType.Error(e)
            }
        }
    }
}