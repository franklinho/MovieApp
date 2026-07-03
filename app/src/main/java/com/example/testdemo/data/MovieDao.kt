package com.example.testdemo.data

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MovieDao {
    @Query("SELECT * FROM MovieDto ORDER BY order_index ASC")
    fun pagingSource(): PagingSource<Int, MovieDto>

    @Query("SELECT MAX(order_index) FROM MovieDto")
    suspend fun maxOrderIndex(): Int?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(movies: List<MovieDto>)

    @Query("DELETE FROM MovieDto")
    suspend fun clearAll()

    @Query("SELECT next_page FROM TrendingRemoteKey WHERE id = :id")
    suspend fun nextTrendingPage(id: String = TRENDING_REMOTE_KEY_ID): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTrendingRemoteKey(remoteKey: TrendingRemoteKey)

    @Query("DELETE FROM TrendingRemoteKey WHERE id = :id")
    suspend fun clearTrendingRemoteKey(id: String = TRENDING_REMOTE_KEY_ID)
}
