package com.rtvplus.data.services

import com.rtvplus.data.models.coupon_payment.RedeemCouponPaymentResponse
import com.rtvplus.data.models.favorite_list.AddListResponse
import com.rtvplus.data.models.favorite_list.FavoriteResponse
import com.rtvplus.data.models.favorite_list.RemoveListResponse
import com.rtvplus.data.models.forgetPassword.ForgetPasswordResponse
import com.rtvplus.data.models.home.HomeResponse
import com.rtvplus.data.models.info.InfoResponse
import com.rtvplus.data.models.local_payment.LocalPaymentResponse
import com.rtvplus.data.models.local_payment.SaveLocalPaymentResponse
import com.rtvplus.data.models.logIn.LogInResponse
import com.rtvplus.data.models.registration.RegistrationResponse
import com.rtvplus.data.models.search.SearchResponse
import com.rtvplus.data.models.seeAll.SeeAllResponse
import com.rtvplus.data.models.single_content.playlist.PlayListResponse
import com.rtvplus.data.models.single_content.single.SingleContentResponse
import com.rtvplus.data.models.socialmedia_login.google.GoogleLogInResponse
import com.rtvplus.data.models.subscription.SubscriptionResponse
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
    @POST("flix_mylist.php")
    suspend fun getFavoriteContent(
        @Field("username") username: String,
        @Field("page") page: String
    ): Response<FavoriteResponse>

    @FormUrlEncoded
    @POST("flix_setmylist.php")
    suspend fun addFavoriteContent(
        @Field("myval") myval: String,
        @Field("username") username: String
    ): Response<AddListResponse>

    @FormUrlEncoded
    @POST("flix_unsetmylist.php")
    suspend fun removeFavoriteContent(
        @Field("myval") myval: String,
        @Field("username") username: String
    ): Response<RemoveListResponse>

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
    ): Response<GoogleLogInResponse>

    @FormUrlEncoded
    @POST("flix_appinfo.php")
    suspend fun getInfoData(
        @Field("msisdn") msisdn: String,
        @Field("appinfo") appinfo: String,
    ): Response<InfoResponse>

    @FormUrlEncoded
    @POST("flix_subscription_status_check_portpos.php")
    suspend fun getSavedLocalPaymentData(
        @Field("msisdn") msisdn: String,
        @Field("paymentId") paymentId: String,
        @Field("orderid") orderid: String
    ): Response<SaveLocalPaymentResponse>

    @FormUrlEncoded
    @POST("flix_sub_instant_sdp_portpos.php")
    suspend fun getLocalPaymentData(
        @Field("msisdn") msisdn: String,
        @Field("d") d: String,
    ): Response<LocalPaymentResponse>

    @FormUrlEncoded
    @POST("flix_sub_instant_coupon.php")
    suspend fun getRedeemCouponPaymentData(
        @Field("msisdn") msisdn: String,
        @Field("couponcode") couponcode: String
    ): Response<RedeemCouponPaymentResponse>
}
