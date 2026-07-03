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
object NetworkModule {

    // MovieService still builds Retrofit/OkHttp/Json + holds the image-URL helper.
    // A later cleanup can inline that here; for now we just expose its MovieApi.
    @Provides
    @Singleton
    fun provideMovieApi(): MovieApi = MovieService().movieApi
}
