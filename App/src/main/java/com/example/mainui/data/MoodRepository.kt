package com.example.mainui.data

import com.example.mainui.data.entities.MoodEntry
import kotlinx.coroutines.flow.Flow

class MoodRepository(private val dao: MoodDao) {
    fun moods(): Flow<List<MoodEntry>> = dao.observeAll()
    suspend fun add(entry: MoodEntry) = dao.insert(entry)
}