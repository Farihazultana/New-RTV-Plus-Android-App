package com.example.rtv_plus_android_app_revamp.data.services

import com.example.rtv_plus_android_app_revamp.data.models.home.HomeResponse
import com.example.rtv_plus_android_app_revamp.data.models.seeAll.SeeAllResponse
import com.example.rtv_plus_android_app_revamp.data.models.subscription.SubscriptionResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiServices {
    @FormUrlEncoded
    @POST("flix_json_app_data_v2.php")
    suspend fun getHomeData(
        @Field("msisdn") msisdn: String,
        @Field("view") view: String
    ): Response<HomeResponse>

    @FormUrlEncoded
    @POST("flix_subschemes.php")
    suspend fun getSubscriptionData(
        @Field("msisdn") msisdn: String
    ): Response<SubscriptionResponse>

    @FormUrlEncoded
    @POST("flixlist_json_app.php")
    suspend fun getSeeAllData(
        @Field("page") page: String,
        @Field("ct") ct:String,
        @Field("tc") tc:String,
        @Field("testval")  testval: String
    ): Response<SeeAllResponse>
}
