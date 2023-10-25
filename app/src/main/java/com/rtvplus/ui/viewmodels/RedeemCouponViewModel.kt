package com.rtvplus.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rtvplus.data.models.coupon_payment.RedeemCuoponPaymentResponse
import com.rtvplus.data.repository.RedeemCouponRepository
import com.rtvplus.utils.ResultType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RedeemCouponViewModel @Inject constructor(private val repository: RedeemCouponRepository): ViewModel(){
    private val _redeemCouponPayemntData = MutableLiveData<ResultType<RedeemCuoponPaymentResponse>>(ResultType.Loading)
    val redeemCuoponPaymentData: MutableLiveData<ResultType<RedeemCuoponPaymentResponse>> = _redeemCouponPayemntData

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