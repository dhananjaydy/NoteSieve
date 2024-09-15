package com.example.notesieve.di

import android.content.Context
import android.content.pm.PackageManager
import androidx.room.AutoMigration
import androidx.room.Room
import androidx.room.migration.AutoMigrationSpec
import com.example.notesieve.PermissionChecker
import com.example.notesieve.data.local.NoteSieveDao
import com.example.notesieve.data.local.NoteSieveDatabase
import com.example.notesieve.data.local.NoteSieveEntity
import com.example.notesieve.data.repository.NoteSieveRepository
import com.example.notesieve.data.repository.NoteSieveRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProvidesModule  {

    @Provides
    @Singleton
    fun provideNoteSieveDatabase(
        @ApplicationContext context: Context
    ): NoteSieveDatabase {
        val instance = Room.databaseBuilder(
           context,
            NoteSieveDatabase::class.java,
            "notification_database"
        )
        .fallbackToDestructiveMigrationOnDowngrade()
        .build()
        return instance
    }

    @Singleton
    @Provides
    fun provideNoteSieveDao(noteSieveDatabase: NoteSieveDatabase): NoteSieveDao
     = noteSieveDatabase.noteSieveDao()

    @Provides
    @Singleton
    fun providePackageManager(@ApplicationContext context: Context): PackageManager = context.packageManager


}