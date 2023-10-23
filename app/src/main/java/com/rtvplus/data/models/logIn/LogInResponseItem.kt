package com.rtvplus.data.models.logIn

data class LogInResponseItem(
    val audioad: Boolean,
    val autorenew: Any,
    val concurrent: Int,
    val concurrenttext: String,
    val consent: Int,
    val consenttext: String,
    val consenturl: String,
    val currentversion: Int,
    val currentversionios: String,
    val email: String,
    val enforce: Int,
    val enforcetext: String,
    val extrainfo: String,
    val fullname: Any,
    val liveurl: String,
    val msisdn: String,
    val packcode: String,
    val packname: String,
    val packtext: String,
    val play: Int,
    val referral: Int,
    val referralimage: String,
    val result: String,
    val showad: Int,
    val token: String,
    val ugc: Int
)