package com.example.youngchemist.di

import com.example.youngchemist.repositories.FireStoreRepository
import com.example.youngchemist.repositories.impl.FireStoreRepositoryImpl
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MainModule {

    @Provides
    @Singleton
    fun provideFirestoreRepository(fireStore: FirebaseFirestore) =
        FireStoreRepositoryImpl(fireStore) as FireStoreRepository
}