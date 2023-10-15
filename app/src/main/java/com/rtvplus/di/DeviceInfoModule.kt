package com.rtvplus.di

import com.rtvplus.data.models.device_info.DeviceInfo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DeviceInfoModule {
    @Provides
    @Singleton
    fun provideDeviceInfo(): DeviceInfo {
        return DeviceInfo()
    }
}