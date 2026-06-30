package com.example.testdemo.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MovieRemoteKeyDao {

    @Query("SELECT * FROM MovieRemoteKey WHERE movieId = :movieId")
    suspend fun remoteKeyByMovieId(movieId: Int): MovieRemoteKey?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKeys: List<MovieRemoteKey>)

    @Query("DELETE FROM MovieRemoteKey")
    suspend fun clearAll()
}
