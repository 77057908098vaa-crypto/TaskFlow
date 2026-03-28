package com.example.taskflow.di

import android.content.Context
import androidx.room.Room
import com.example.taskflow.data.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "taskflow.db").build()

    @Provides
    fun provideTaskDao(db: AppDatabase) = db.taskDao()

    @Provides
    fun provideProjectDao(db: AppDatabase) = db.projectDao()
}
