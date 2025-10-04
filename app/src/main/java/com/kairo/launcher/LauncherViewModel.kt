package com.kairo.launcher

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kairo.launcher.model.InstalledApp
import com.kairo.launcher.util.AppsQuery
import com.kairo.launcher.data.Prefs
import com.kairo.launcher.data.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.datastore.preferences.core.edit

class LauncherViewModel : ViewModel() {

    // Apps + search
    private val _apps = MutableStateFlow<List<InstalledApp>>(emptyList())
    val apps: StateFlow<List<InstalledApp>> = _apps

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    // Settings / favorites (backed by DataStore)
    private val _favorites = MutableStateFlow<Set<String>>(emptySet())
    val favorites: StateFlow<Set<String>> = _favorites

    private val _gridSize = MutableStateFlow(84) // dp
    val gridSize: StateFlow<Int> = _gridSize

    private val _accentHex = MutableStateFlow("#7C5CFF")
    val accentHex: StateFlow<String> = _accentHex

    fun load(context: Context) {
        viewModelScope.launch(Dispatchers.Default) {
            _apps.value = AppsQuery.loadLaunchable(context)
        }
    }

    fun setQuery(q: String) { _query.value = q }

    fun filtered(): List<InstalledApp> =
        if (query.value.isBlank()) apps.value
        else apps.value.filter {
            it.label.contains(query.value, true) || it.packageName.contains(query.value, true)
        }

    fun launch(context: Context, app: InstalledApp) {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            component = ComponentName(app.packageName, app.className)
        }
        context.startActivity(intent)
    }

    /** Start observing DataStore-backed preferences. Call once (e.g., from Activity.onCreate). */
    fun observePrefs(ctx: Context) {
        // Favorites
        viewModelScope.launch(Dispatchers.IO) {
            ctx.dataStore.data.collectLatest { prefs ->
                _favorites.value = prefs[Prefs.FAVORITES] ?: emptySet()
            }
        }
        // Grid size
        viewModelScope.launch(Dispatchers.IO) {
            ctx.dataStore.data.collectLatest { prefs ->
                _gridSize.value = prefs[Prefs.GRID_SIZE] ?: 84
            }
        }
        // Accent
        viewModelScope.launch(Dispatchers.IO) {
            ctx.dataStore.data.collectLatest { prefs ->
                _accentHex.value = prefs[Prefs.ACCENT_HEX] ?: "#7C5CFF"
            }
        }
    }

    fun toggleFavorite(ctx: Context, pkg: String) {
        viewModelScope.launch(Dispatchers.IO) {
            ctx.dataStore.edit { prefs ->
                val cur = prefs[Prefs.FAVORITES] ?: emptySet()
                prefs[Prefs.FAVORITES] = if (pkg in cur) cur - pkg else cur + pkg
            }
        }
    }

    fun setGridSize(ctx: Context, size: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            ctx.dataStore.edit { it[Prefs.GRID_SIZE] = size.coerceIn(64, 120) }
        }
    }

    fun setAccent(ctx: Context, hex: String) {
        viewModelScope.launch(Dispatchers.IO) {
            ctx.dataStore.edit { it[Prefs.ACCENT_HEX] = hex }
        }
    }
}
