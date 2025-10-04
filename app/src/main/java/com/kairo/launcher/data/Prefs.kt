package com.kairo.launcher.data

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore by preferencesDataStore("kairo_prefs")

object Prefs {
    val FAVORITES = stringSetPreferencesKey("favorites")   // set of package names
    val GRID_SIZE = intPreferencesKey("grid_size")         // e.g., 72..120 (dp min cell)
    val ACCENT_HEX = stringPreferencesKey("accent_hex")    // e.g., "#7C5CFF"
}
