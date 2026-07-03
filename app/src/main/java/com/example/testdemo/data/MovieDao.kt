package com.example.testdemo.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query("SELECT * FROM MovieDto")
    fun observeAll(): Flow<List<MovieDto>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(movies: List<MovieDto>)

    @Query("DELETE FROM MovieDto")
    suspend fun deleteAll()

    @Transaction
    suspend fun replaceAll(movies: List<MovieDto>) {
        deleteAll()
        insertAll(movies)
    }
}
