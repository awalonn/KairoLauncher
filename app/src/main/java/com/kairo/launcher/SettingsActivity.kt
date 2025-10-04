
package com.kairo.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.kairo.launcher.ui.theme.KairoTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.graphics.Color

class SettingsActivity : ComponentActivity() {
    private val vm: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm.observePrefs(this)
        setContent {
            KairoTheme {
                val grid by vm.gridSize.collectAsState()
                val accent by vm.accentHex.collectAsState()
                var hex by remember { mutableStateOf(TextFieldValue(accent)) }

                Column(Modifier.fillMaxSize().padding(16.dp)) {
                    Text("Kairo Settings", style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(16.dp))

                    Text("Grid size (cell min dp): $grid")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(72, 84, 100, 112).forEach { s ->
                            Button(onClick = { vm.setGridSize(this@SettingsActivity, s) }) {
                                Text("$s")
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = hex,
                        onValueChange = { hex = it },
                        label = { Text("Accent hex (e.g., #7C5CFF)") }
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { vm.setAccent(this@SettingsActivity, hex.text) }) {
                        Text("Save Accent")
                    }
                    Spacer(Modifier.height(12.dp))
                    val preview = runCatching { androidx.compose.ui.graphics.Color(android.graphics.Color.parseColor(hex.text)) }
                        .getOrElse { MaterialTheme.colorScheme.primary }
                    Box(
                        Modifier
                            .size(36.dp)
                            .background(preview, shape = MaterialTheme.shapes.small)
                    )
                }
            }
        }
    }
}
