package com.example.rtv_plus_android_app_revamp.data.repository

import android.util.Log
import com.example.rtv_plus_android_app_revamp.data.models.coupon_payment.RedeemCouponPaymentResponse
import com.example.rtv_plus_android_app_revamp.data.services.ApiServices
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import javax.inject.Inject

class RedeemCouponRepository @Inject constructor(private val apiServices: ApiServices){
    suspend fun getRedeemCouponPaymentData(msisdn: String, couponcode: String): ResultType<RedeemCouponPaymentResponse>{
        try {
            val response = apiServices.getRedeemCouponPaymentData(msisdn, couponcode)
            Log.i("Redeem", "getRedeemCouponPaymentData: successful Api call ${response.code()}")
            if (response.isSuccessful){
                val data = response.body()
                if (response.isSuccessful){
                    val data = response.body()
                    if (data!= null){
                        Log.i("Redeem", "getRedeemCouponPaymentData: $data")
                    }
                    if (data != null){
                        return ResultType.Success(data)
                    }
                }

            }
            return ResultType.Error(Exception("Failed to fetch Redeem Couopon Payment data"))
        }catch (e: Exception){
            return ResultType.Error(e)
        }

    }
}