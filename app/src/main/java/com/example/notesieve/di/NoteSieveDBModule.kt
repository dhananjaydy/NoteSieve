package com.example.notesieve.di

import com.example.notesieve.data.repository.NoteSieveRepository
import com.example.notesieve.data.repository.NoteSieveRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NoteSieveDBModule {

    @Binds
    @Singleton
    abstract fun bindNoteSieveRepo(noteSieveRepositoryImpl: NoteSieveRepositoryImpl): NoteSieveRepository

}