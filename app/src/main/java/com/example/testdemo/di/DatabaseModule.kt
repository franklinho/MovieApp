package com.example.testdemo.di

import android.content.Context
import androidx.room.Room
import com.example.testdemo.data.AppDatabase
import com.example.testdemo.data.MovieDao
import com.example.testdemo.data.MovieRemoteKeyDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "movies.db")
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .build()

    @Provides
    fun provideMovieDao(database: AppDatabase): MovieDao = database.movieDao()

    @Provides
    fun provideMovieRemoteKeyDao(database: AppDatabase): MovieRemoteKeyDao =
        database.movieRemoteKeyDao()
}
