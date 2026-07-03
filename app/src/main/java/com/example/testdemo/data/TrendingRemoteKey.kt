package com.example.testdemo.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TrendingRemoteKey(
    @PrimaryKey val id: String = TRENDING_REMOTE_KEY_ID,
    @ColumnInfo(name = "next_page") val nextPage: Int?,
)

const val TRENDING_REMOTE_KEY_ID = "trending"
