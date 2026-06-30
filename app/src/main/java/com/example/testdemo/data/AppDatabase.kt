package com.example.testdemo.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Movie::class, MovieRemoteKey::class], version = 2, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun movieRemoteKeyDao(): MovieRemoteKeyDao

    companion object {
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `MovieRemoteKey` (
                        `movieId` INTEGER NOT NULL,
                        `prevKey` INTEGER,
                        `nextKey` INTEGER,
                        PRIMARY KEY(`movieId`)
                    )
                    """.trimIndent()
                )
            }
        }
    }
}
