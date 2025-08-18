package com.example.mainui.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mood_entries")
data class MoodEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val timestamp: Long,           // System.currentTimeMillis()
    val emotion: String,           // "Happy", "Sad", etc.
    val score: Int,                // 1..5 for trend graph
    val notes: String
)