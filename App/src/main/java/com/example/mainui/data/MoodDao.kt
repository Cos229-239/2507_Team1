package com.example.mainui.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RoomDatabase
import com.example.mainui.data.entities.MoodEntry
import com.example.mainui.data.entities.User
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodDao {
    @Insert
    suspend fun insert(entry: MoodEntry)

    @Query("SELECT * FROM mood_entries ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<MoodEntry>>
}