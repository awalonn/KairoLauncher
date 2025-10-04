package com.kairo.launcher

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kairo.launcher.model.InstalledApp
import com.kairo.launcher.util.AppsQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
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
}