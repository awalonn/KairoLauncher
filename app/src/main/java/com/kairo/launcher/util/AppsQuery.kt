package com.kairo.launcher.util

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import com.kairo.launcher.model.InstalledApp

object AppsQuery {
    fun loadLaunchable(context: Context): List<InstalledApp> {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val infos: List<ResolveInfo> = pm.queryIntentActivities(intent, 0)
        return infos.map { ri ->
            val label = ri.loadLabel(pm).toString()
            val icon = ri.loadIcon(pm)
            val pkg = ri.activityInfo.packageName
            val cls = ri.activityInfo.name
            InstalledApp(label, pkg, cls, icon)
        }.sortedBy { it.label.lowercase() }
    }
}