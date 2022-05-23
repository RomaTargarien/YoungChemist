package com.chemist.youngchemist.di

import android.content.Context
import com.chemist.youngchemist.repositories.AuthRepository
import com.chemist.youngchemist.repositories.impl.AuthRepositoryImpl
import com.chemist.youngchemist.ui.base.validation.ValidationImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
        ValidationImpl.LoginValidation(context)

    @Provides
    @Singleton
    fun providePasswordValidation(@ApplicationContext context: Context) =
        ValidationImpl.PasswordValidation(context)

    @Provides
    @Singleton
    fun provideNameValidation(@ApplicationContext context: Context) =
        ValidationImpl.NameValidation(context)

    @Provides
    @Singleton
    fun provideSurnameValidation(@ApplicationContext context: Context) =
        ValidationImpl.SurnameValidation(context)


    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore
    ) = AuthRepositoryImpl(firebaseAuth, firestore) as AuthRepository

    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirestore() = FirebaseFirestore.getInstance()

}