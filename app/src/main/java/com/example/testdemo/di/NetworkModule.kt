package com.example.testdemo.di

import com.example.testdemo.networking.MovieApi
import com.example.testdemo.networking.MovieService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Provides
    @Singleton
    fun provideMovieApi(): MovieApi = MovieService().movieApi
}