package com.kairo.launcher.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("kairo_prefs")

object Prefs {
    val FAVORITES = stringSetPreferencesKey("favorites")
    val GRID_SIZE = intPreferencesKey("grid_size")          // 72..120 (dp min cell)
    val ACCENT_HEX = stringPreferencesKey("accent_hex")     // e.g. "#7C5CFF"
}
