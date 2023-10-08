package com.example.rtv_plus_android_app_revamp.data.repository

import android.util.Log
import com.example.rtv_plus_android_app_revamp.data.models.local_payment.SaveLocalPaymentResponse
import com.example.rtv_plus_android_app_revamp.data.services.ApiServices
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import javax.inject.Inject

class SaveLocalPaymentRepository @Inject constructor(private val apiServices: ApiServices){
    suspend fun getSavedLocalPaymentData(msisdn : String, paymentId: String, orderid: String): ResultType<SaveLocalPaymentResponse>{
        try {
            val response = apiServices.getSavedLocalPaymentData(msisdn, paymentId, orderid)
            Log.i("TAG", "successful api call: ${response.code()}")
            if (response.isSuccessful){
                val data = response.body()
                Log.i("Payment", "getSavedLocalPaymentData: $data")
                if (data != null){
                    return ResultType.Success(data)
                }
            }
            return ResultType.Error(Exception("Failed to fetch SavedLocalPayment data"))
        }catch (e: Exception){
            return ResultType.Error(e)
        }
    }
}