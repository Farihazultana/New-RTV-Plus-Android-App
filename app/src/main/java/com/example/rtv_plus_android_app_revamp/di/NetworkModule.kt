package com.example.rtv_plus_android_app_revamp.di

import androidx.lifecycle.ViewModelProvider
import com.example.rtv_plus_android_app_revamp.data.repository.HomeRepository
import com.example.rtv_plus_android_app_revamp.data.repository.SingleContentRepository
import com.example.rtv_plus_android_app_revamp.data.services.ApiServices
import com.example.rtv_plus_android_app_revamp.ui.viewmodels.HomeViewModel
import com.example.rtv_plus_android_app_revamp.ui.viewmodels.SingleContentViewModel
import com.example.rtv_plus_android_app_revamp.data.services.ApiServices
import com.example.rtv_plus_android_app_revamp.utils.AppUtils.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {
    @Singleton
    @Provides
    fun providesRetrofit(): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
    }
    @Singleton
    @Provides
    fun providesApiService(retrofit: Retrofit): ApiServices {
        return retrofit.create(ApiServices::class.java)
    }

}