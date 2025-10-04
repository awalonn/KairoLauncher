
package com.kairo.launcher.data

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore by preferencesDataStore("kairo_prefs")

object Prefs {
    val FAVORITES = stringSetPreferencesKey("favorites")
    val GRID_SIZE = intPreferencesKey("grid_size")
    val ACCENT_HEX = stringPreferencesKey("accent_hex")
}
