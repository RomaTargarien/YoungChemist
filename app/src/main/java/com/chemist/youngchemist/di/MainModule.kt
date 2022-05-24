package com.chemist.youngchemist.di

import android.content.Context
import android.content.res.Resources
import androidx.room.Room
import androidx.work.WorkManager
import com.chemist.youngchemist.db.SubjectDatabase
import com.chemist.youngchemist.db.dao.*
import com.chemist.youngchemist.db.shared_pref.UserPreferenceImpl
import com.chemist.youngchemist.db.shared_pref.UserPreferences
import com.chemist.youngchemist.repositories.DatabaseRepository
import com.chemist.youngchemist.repositories.FireStoreRepository
import com.chemist.youngchemist.repositories.impl.DatabaseRepositoryImpl
import com.chemist.youngchemist.repositories.impl.FireStoreRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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
        userProgressDao: UserProgressDao,
        achievementsDao: AchievementsDao
    ) =
        DatabaseRepositoryImpl(
            subjectDao,
            lectureDao,
            testDao,
            model3DDao,
            userProgressDao,
            achievementsDao
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
    fun provideSubjectsDao(db: SubjectDatabase) = db.getSubjectDao()

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
    fun provideAchievementsDao(db: SubjectDatabase) = db.getAchievementsDao()

    @Provides
    @Singleton
    fun provideUserPreference(@ApplicationContext context: Context) =
        UserPreferenceImpl(context) as UserPreferences

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager =
        WorkManager.getInstance(context)

    @Provides
    fun provideCurrentUser(): FirebaseUser = FirebaseAuth.getInstance().currentUser!!

    @Provides
    @Singleton
    fun provideResources(@ApplicationContext context: Context): Resources = context.resources


}