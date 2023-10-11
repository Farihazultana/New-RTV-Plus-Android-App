package com.rtvplus.data.repository

import android.util.Log
import com.rtvplus.data.models.local_payment.LocalPaymentResponse
import com.rtvplus.data.services.ApiServices
import com.rtvplus.utils.ResultType
import javax.inject.Inject

class LocalPaymentRepository @Inject constructor(private val apiServices: ApiServices) {
    suspend fun getLocalPaymentData(msisdn: String, d: String): ResultType<LocalPaymentResponse> {
        try {
            val response = apiServices.getLocalPaymentData(msisdn, d)
            Log.i("Payment", "successful api call: ${response.code()} ")
            if (response.isSuccessful) {
                val data = response.body()
                if (data != null) {
                    Log.i("Payment", "getLocalPaymentData: $data")
                }
                if (data != null) {
                    return ResultType.Success(data)
                }
            }
            return ResultType.Error(Exception("Failed to fetch Local Payment data"))
        } catch (e: Exception) {
            return ResultType.Error(e)
        }
    }
}