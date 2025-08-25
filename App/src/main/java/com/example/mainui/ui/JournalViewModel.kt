package com.example.mainui.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mainui.data.AppDatabase
import com.example.mainui.data.entities.JournalEntry
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class JournalViewModel(app: Application) : AndroidViewModel(app) {

    private val db = AppDatabase.get(app)
    private val dao = db.journalDao()

    val entries = dao.observeAll()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addEntry(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            dao.insert(
                JournalEntry(
                    timestamp = System.currentTimeMillis(),
                    text = text.trim()
                )
            )
        }
    }
}