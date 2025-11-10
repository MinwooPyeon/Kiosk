package com.harang.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location")
data class LocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String, // 예: "TEST_LIST_SCREEN", "SCREENSAVER"
    val mediaType: MediaType // IMAGE 또는 VIDEO
)