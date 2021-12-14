package com.example.youngchemist.di

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.example.youngchemist.db.SubjectDatabase
import com.example.youngchemist.db.dao.*
import com.example.youngchemist.db.shared_pref.UserPreferenceImpl
import com.example.youngchemist.db.shared_pref.UserPreferences
import com.example.youngchemist.repositories.DatabaseRepository
import com.example.youngchemist.repositories.FireStoreRepository
import com.example.youngchemist.repositories.impl.DatabaseRepositoryImpl
import com.example.youngchemist.repositories.impl.FireStoreRepositoryImpl
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MainModule {

    @Provides
    @Singleton
    fun provideFirestoreRepository(fireStore: FirebaseFirestore, storage: FirebaseStorage) =
        FireStoreRepositoryImpl(fireStore, storage) as FireStoreRepository

    @Provides
    @Singleton
    fun provideDatabaseRepository(
        subjectDao: SubjectDao,
        lectureDao: LectureDao,
        testDao: TestDao,
        model3DDao: Model3DDao,
        userProgressDao: UserProgressDao
    ) =
        DatabaseRepositoryImpl(
            subjectDao,
            lectureDao,
            testDao,
            model3DDao,
            userProgressDao
        ) as DatabaseRepository


    @Provides
    @Singleton
    fun provideFirebaseStorage() = FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideSubjectDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context, SubjectDatabase::class.java, "db"
    ).fallbackToDestructiveMigration().build()

    @Provides
    @Singleton
    fun provideSubjectsDao(db: SubjectDatabase) = db.getSubjectdao()

    @Provides
    @Singleton
    fun provideLecturesDao(db: SubjectDatabase) = db.getLectureDao()

    @Provides
    @Singleton
    fun provideTestDao(db: SubjectDatabase) = db.getTestDao()

    @Provides
    @Singleton
    fun provideModel3DDao(db: SubjectDatabase) = db.getModel3DDao()

    @Provides
    @Singleton
    fun provideUserProgressDao(db: SubjectDatabase) = db.getUserProgressDao()

    @Provides
    @Singleton
    fun provideUserPreference(@ApplicationContext context: Context) =
        UserPreferenceImpl(context) as UserPreferences

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager = WorkManager.getInstance(context)


}