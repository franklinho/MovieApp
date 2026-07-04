package com.example.testdemo

import android.content.Context
import androidx.room.Room
import com.example.testdemo.data.AppDatabase
import com.example.testdemo.data.MovieRepository
import com.example.testdemo.networking.MovieApi
import com.example.testdemo.networking.MovieService

class AppContainer(context: Context) {
    private val applicationContext = context.applicationContext

    val movieApi: MovieApi by lazy {
        MovieService().movieApi
    }

    val database: AppDatabase by lazy {
        Room.databaseBuilder(applicationContext, AppDatabase::class.java, "movies.db")
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    val movieRepository: MovieRepository by lazy {
        MovieRepository(
            movieApi = movieApi,
            database = database,
            movieDao = database.movieDao(),
        )
    }
}
