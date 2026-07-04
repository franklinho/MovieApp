package com.example.testdemo.data

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MovieDao {

    @Query("SELECT * FROM Movie ORDER BY order_index ASC")
    fun pagingSource(): PagingSource<Int, MovieDto>

    @Query("SELECT COUNT(*) FROM Movie")
    suspend fun count(): Int

    @Query("SELECT * FROM Movie WHERE id = :id LIMIT 1")
    suspend fun findById(id: Int): MovieDto?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(movies: List<MovieDto>)

    @Query("DELETE FROM Movie")
    suspend fun clearAll()
}
