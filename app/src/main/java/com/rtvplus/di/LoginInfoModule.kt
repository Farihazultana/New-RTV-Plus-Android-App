package com.rtvplus.di

import com.rtvplus.data.models.logIn.LogInResponse
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LoginInfoModule {
    @Provides
    @Singleton
    fun provideLoginInfo() : LogInResponse{
        return LogInResponse()
    }
}