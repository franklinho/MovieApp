package com.example.testdemo.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DatabaseHelperImpl(private val appDatabase: AppDatabase) : DatabaseHelper {

    override fun getMovie(id: String): Flow<Movie> = flow {
        emit(appDatabase.movieDao().getMovie(id))
    }

    override fun getMovies(): Flow<List<Movie>>  = flow {
        emit(appDatabase.movieDao().getAll())
    }

    override fun insertAll(movies: List<Movie>): Flow<Unit> = flow {
        appDatabase.movieDao().insertAll(movies)
        emit(Unit)
    }

    override fun deleteAll(): Flow<Unit> = flow {
        appDatabase.movieDao().deleteAll()
        emit(Unit)
    }
}