package com.rtvplus.data.models.local_payment

data class LocalPaymentResponseItem(
    val play: Int,
    val response: String,
    val sdpurl: String,
    val userdetail: String,
    val userinfo: String
)