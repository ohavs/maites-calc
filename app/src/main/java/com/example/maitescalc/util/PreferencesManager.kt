package com.example.maitescalc.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesManager(private val context: Context) {
    companion object {
        val SELECTED_PALETTE_KEY = stringPreferencesKey("selected_palette")
    }

    val selectedPaletteFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[SELECTED_PALETTE_KEY] ?: "lime"
    }

    suspend fun setSelectedPalette(paletteId: String) {
        context.dataStore.edit { preferences ->
            preferences[SELECTED_PALETTE_KEY] = paletteId
        }
    }
}
