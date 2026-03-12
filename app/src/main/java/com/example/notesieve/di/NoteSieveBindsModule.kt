package com.example.notesieve.di

import com.example.notesieve.data.repository.LinkHandlerRepository
import com.example.notesieve.data.repository.LinkHandlerRepositoryImpl
import com.example.notesieve.data.repository.NoteSieveRepository
import com.example.notesieve.data.repository.NoteSieveRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NoteSieveBindsModule {

    @Binds
    @Singleton
    abstract fun bindNoteSieveRepo(
        noteSieveRepositoryImpl: NoteSieveRepositoryImpl
    ): NoteSieveRepository

    @Binds
    @Singleton
    abstract fun bindLinkRepository(
        linkHandlerRepositoryImpl: LinkHandlerRepositoryImpl
    ): LinkHandlerRepository

}