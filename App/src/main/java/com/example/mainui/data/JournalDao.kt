package com.example.mainui.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.mainui.data.entities.JournalEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalDao {
    @Insert
    suspend fun insert(entry: JournalEntry)

    @Query("SELECT * FROM journal_entries ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<JournalEntry>>
}