package com.example.testdemo.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Movie::class, MovieRemoteKey::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun movieRemoteKeyDao(): MovieRemoteKeyDao
}
