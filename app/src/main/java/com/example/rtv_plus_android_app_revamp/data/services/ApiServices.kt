package com.example.rtv_plus_android_app_revamp.data.services

import com.example.rtv_plus_android_app_revamp.data.models.forgetPassword.ForgetPasswordResponse
import com.example.rtv_plus_android_app_revamp.data.models.home.HomeResponse
import com.example.rtv_plus_android_app_revamp.data.models.local_payment.LocalPaymentResponse
import com.example.rtv_plus_android_app_revamp.data.models.logIn.LogInResponse
import com.example.rtv_plus_android_app_revamp.data.models.registration.RegistrationResponse
import com.example.rtv_plus_android_app_revamp.data.models.search.SearchResponse
import com.example.rtv_plus_android_app_revamp.data.models.single_content.playlist.PlayListResponse
import com.example.rtv_plus_android_app_revamp.data.models.single_content.single.SingleContentResponse
import com.example.rtv_plus_android_app_revamp.data.models.seeAll.SeeAllResponse
import com.example.rtv_plus_android_app_revamp.data.models.socialmedia_login.google.GoogleLogInResponse
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
    @POST("flixlist_json_app_single_2.php")
    suspend fun getSingleData(
        @Field("msisdn") msisdn: String,
        @Field("cc") cc: String,
        @Field("fromsrc") fromsrc: String
    ): Response<SingleContentResponse>

    @FormUrlEncoded
    @POST("flix_json_app_dramaserial.php")
    suspend fun getPlayListData(
        @Field("username") username: String,
        @Field("cc") cc: String,
        @Field("resolution") resolution: String
    ): Response<PlayListResponse>

    @FormUrlEncoded

    @POST("flix_src_app.php")
    suspend fun getSearchResultData(
        @Field("fromsrc") fromsrc: String,
        @Field("s") s: String
    ): Response<SearchResponse>

    @FormUrlEncoded
    @POST("flix_subschemes.php")
    suspend fun getSubscriptionData(
        @Field("msisdn") msisdn: String
    ): Response<SubscriptionResponse>

    @FormUrlEncoded
    @POST("flixlist_json_app.php")
    suspend fun getSeeAllData(
        @Field("page") page: String,
        @Field("ct") ct: String,
        @Field("tc") tc: String,
        @Field("testval") testval: String
    ): Response<SeeAllResponse>

    @FormUrlEncoded
    @POST("flix_makemylogingettoken.php")
    suspend fun getLogInData(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("haspin") haspin: String,
        @Field("tc") tc: String,
    ): Response<LogInResponse>

    @FormUrlEncoded
    @POST("flix_signup.php")
    suspend fun getRegistrationData(
        @Field("msisdn") msisdn: String
    ): Response<RegistrationResponse>

    @FormUrlEncoded
    @POST("flix_change_password.php")
    suspend fun getForgetPasswordData(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("newpass") newpass: String
    ): Response<ForgetPasswordResponse>

    @FormUrlEncoded
    @POST("flix_social_login.php")
    suspend fun getGoogleLogInData(
        @Field("logintype") logintype: String,
        @Field("source") source: String,
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("first_name") first_name: String,
        @Field("last_name") last_namez: String,
        @Field("email") email: String,
        @Field("imgurl") imgurl: String
    ):Response<GoogleLogInResponse>

    @FormUrlEncoded
    @POST("flix_sub_instant_sdp_portpos.php")
    suspend fun getLocalPaymentData(
        @Field("msisdn") msisdn: String,
        @Field("d") d: String,
    ): Response<LocalPaymentResponse>
}
