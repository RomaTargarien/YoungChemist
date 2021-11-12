package com.example.youngchemist.di

import android.content.Context
import com.example.youngchemist.ui.base.validation.ValidationImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideEmailValidation(@ApplicationContext context: Context) =
        ValidationImpl.EmailValidation(context)

    @Provides
    @Singleton
    fun providePasswordValidation(@ApplicationContext context: Context) =
        ValidationImpl.PasswordValidation(context)
}