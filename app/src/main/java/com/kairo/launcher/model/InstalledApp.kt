package com.kairo.launcher.model

import android.graphics.drawable.Drawable

data class InstalledApp(
    val label: String,
    val packageName: String,
    val className: String,
    val icon: Drawable
)