package com.example.mainui.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "app_preferences")

class PreferencesManager(private val context: Context) {
    private val MOOD_KEY = stringPreferencesKey("mood")

    val mood: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[MOOD_KEY]
    }

    suspend fun saveMood(moodValue: String) {
        context.dataStore.edit { prefs ->
            prefs[MOOD_KEY] = moodValue
        }
    }
}