package com.kairo.launcher.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.kairo.launcher.model.InstalledApp

@Composable
fun AppGrid(apps: List<InstalledApp>, onOpen: (InstalledApp) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 72.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp)
    ) {
        items(apps) { app ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(8.dp)
                    .width(84.dp)
            ) {
                FilledTonalButton(
                    onClick = { onOpen(app) },
                    modifier = Modifier.size(64.dp)
                ) {
                    Image(bitmap = app.icon.toBitmap().asImageBitmap(), contentDescription = app.label)
                }
                Spacer(Modifier.height(6.dp))
                Text(app.label, maxLines = 1)
            }
        }
    }
}