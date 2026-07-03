package com.example.testdemo.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MovieDto::class, TrendingRemoteKey::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}
