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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LauncherViewModel: ViewModel() {
    private val _apps = MutableStateFlow<List<InstalledApp>>(emptyList())
    val apps: StateFlow<List<InstalledApp>> = _apps

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    fun load(context: Context) {
        viewModelScope.launch(Dispatchers.Default) {
            _apps.value = AppsQuery.loadLaunchable(context)
        }
    }

    fun setQuery(q: String) { _query.value = q }

    fun filtered(): List<InstalledApp> =
        if (query.value.isBlank()) apps.value
        else apps.value.filter { it.label.contains(query.value, true) || it.packageName.contains(query.value, true) }

    fun launch(context: Context, app: InstalledApp) {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            component = ComponentName(app.packageName, app.className)
        }
        context.startActivity(intent)
    }
    
    // Favorites (package names)
    private val _favorites = MutableStateFlow<Set<String>>(emptySet())
    val favorites: StateFlow<Set<String>> = _favorites

    // Settings
    private val _gridSize = MutableStateFlow(84)         // dp min cell
    val gridSize: StateFlow<Int> = _gridSize
    private val _accentHex = MutableStateFlow("#7C5CFF")
    val accentHex: StateFlow<String> = _accentHex

    fun observePrefs(ctx: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            ctx.dataStore.data.map { it[Prefs.FAVORITES] ?: emptySet() }.collect { _favorites.value = it }
        }
        viewModelScope.launch(Dispatchers.IO) {
            ctx.dataStore.data.map { it[Prefs.GRID_SIZE] ?: 84 }.collect { _gridSize.value = it }
        }
        viewModelScope.launch(Dispatchers.IO) {
            ctx.dataStore.data.map { it[Prefs.ACCENT_HEX] ?: "#7C5CFF" }.collect { _accentHex.value = it }
        }
    }

    fun toggleFavorite(ctx: Context, pkg: String) {
        viewModelScope.launch(Dispatchers.IO) {
            ctx.dataStore.updateData { prefs ->
                val cur = prefs[Prefs.FAVORITES] ?: emptySet()
                prefs.toMutablePreferences().apply {
                    this[Prefs.FAVORITES] = if (pkg in cur) cur - pkg else cur + pkg
                }
            }
        }
    }

    fun setGridSize(ctx: Context, size: Int) = viewModelScope.launch(Dispatchers.IO) {
        ctx.dataStore.updateData { it.toMutablePreferences().apply { this[Prefs.GRID_SIZE] = size } }
    }

    fun setAccent(ctx: Context, hex: String) = viewModelScope.launch(Dispatchers.IO) {
        ctx.dataStore.updateData { it.toMutablePreferences().apply { this[Prefs.ACCENT_HEX] = hex } }
    }
}
