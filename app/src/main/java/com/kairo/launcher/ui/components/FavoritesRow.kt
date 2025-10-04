package com.kairo.launcher.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.kairo.launcher.model.InstalledApp

@Composable
fun FavoritesRow(favs: List<InstalledApp>, onOpen: (InstalledApp) -> Unit) {
    if (favs.isEmpty()) return
    LazyRow(
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(favs) { app ->
            ElevatedButton(onClick = { onOpen(app) }) {
                Image(app.icon.toBitmap().asImageBitmap(), null, Modifier.size(24.dp))
                Spacer(Modifier.width(8.dp))
                Text(app.label, maxLines = 1)
            }
        }
    }
}
