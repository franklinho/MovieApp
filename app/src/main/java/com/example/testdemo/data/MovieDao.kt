package com.example.testdemo.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Query("SELECT * FROM Movie")
    fun observeAll(): Flow<List<Movie>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(movies: List<Movie>)

    @Query("DELETE FROM Movie")
    suspend fun deleteAll()

    @Transaction
    suspend fun replaceAll(movies: List<Movie>) {
        deleteAll()
        insertAll(movies)
    }
}
