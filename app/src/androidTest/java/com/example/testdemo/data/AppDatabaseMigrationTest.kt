package com.example.testdemo.data

import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDatabaseMigrationTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setUp() {
        context.deleteDatabase(TEST_DB)
    }

    @After
    fun tearDown() {
        context.deleteDatabase(TEST_DB)
    }

    @Test
    fun migration1To2PreservesMoviesAndAddsRemoteKeys() = runBlocking {
        createVersion1Database()

        val database = Room.databaseBuilder(context, AppDatabase::class.java, TEST_DB)
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .allowMainThreadQueries()
            .build()

        try {
            assertEquals(1, database.movieDao().count())
            assertNull(database.movieRemoteKeyDao().remoteKeyByMovieId(1))

            val key = MovieRemoteKey(movieId = 1, prevKey = null, nextKey = 2)
            database.movieRemoteKeyDao().insertAll(listOf(key))

            assertEquals(key, database.movieRemoteKeyDao().remoteKeyByMovieId(1))
        } finally {
            database.close()
        }
    }

    private fun createVersion1Database() {
        val databaseFile = context.getDatabasePath(TEST_DB)
        databaseFile.parentFile?.mkdirs()

        val database = SQLiteDatabase.openOrCreateDatabase(databaseFile, null)
        try {
            database.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `Movie` (
                    `id` INTEGER NOT NULL,
                    `title` TEXT,
                    `overview` TEXT,
                    `adult` INTEGER NOT NULL,
                    `poster_path` TEXT,
                    `backdrop_path` TEXT,
                    `order_index` INTEGER NOT NULL,
                    PRIMARY KEY(`id`)
                )
                """.trimIndent()
            )
            database.execSQL(
                """
                INSERT INTO `Movie` (
                    `id`,
                    `title`,
                    `overview`,
                    `adult`,
                    `poster_path`,
                    `backdrop_path`,
                    `order_index`
                ) VALUES (1, 'Inception', 'Dream-sharing technology.', 0, '/poster.jpg', '/backdrop.jpg', 0)
                """.trimIndent()
            )
            database.setVersion(1)
        } finally {
            database.close()
        }
    }

    private companion object {
        const val TEST_DB = "migration-test.db"
    }
}
