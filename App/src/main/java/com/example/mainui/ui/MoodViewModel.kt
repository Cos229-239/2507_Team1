package com.example.mainui.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mainui.data.AppDatabase
import com.example.mainui.data.MoodDao
import com.example.mainui.data.entities.MoodEntry
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MoodViewModel(app: Application) : AndroidViewModel(app) {
    private val dao: MoodDao = AppDatabase.get(app).moodDao()

    // Stream of all entries (newest first)
    val entries: StateFlow<List<MoodEntry>> =
        dao.observeAll().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun addEntry(emotion: String, score: Int, notes: String) {
        viewModelScope.launch {
            dao.insert(
                MoodEntry(
                    timestamp = System.currentTimeMillis(),
                    emotion = emotion,
                    score = score,
                    notes = notes.trim()
                )
            )
        }
    }
}