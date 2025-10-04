package com.kairo.launcher

import android.app.role.RoleManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.kairo.launcher.ui.components.AppGrid
import com.kairo.launcher.ui.components.RadialDock
import com.kairo.launcher.ui.components.SearchBar
import com.kairo.launcher.ui.theme.KairoTheme

class MainActivity : ComponentActivity() {
    private val vm: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm.load(this)
        setContent { KairoApp(vm) }
    }
}

@Composable
fun KairoApp(vm: LauncherViewModel) {
    KairoTheme {
        val ctx = LocalContext.current
        val query by vm.query.collectAsState()
        Scaffold(
            topBar = {
                Column(Modifier.fillMaxWidth()) {
                    SearchBar(query) { vm.setQuery(it) }
                    DefaultHomeHint()
                }
            },
            bottomBar = {
                RadialDock { slot ->
                    when(slot) {
                        0 -> { /* focus search */ }
                        1 -> { /* app drawer is main view here */ }
                        2 -> openRecents(ctx) // placeholder
                        3 -> openSettings(ctx)
                    }
                }
            }
        ) { paddings ->
            AppGrid(vm.filtered()) { vm.launch(ctx, it) }
        }
    }
}

@Composable
fun DefaultHomeHint() {
    val ctx = LocalContext.current
    if (!isDefaultHome(ctx)) {
        Surface(color = MaterialTheme.colorScheme.surfaceVariant) {
            Row(Modifier.fillMaxWidth().padding(12.dp)) {
                Text("Set Kairo as your default Home for the full experience.")
                Spacer(Modifier.width(12.dp))
                TextButton(onClick = { requestHomeRole(ctx) }) { Text("Set Default") }
            }
        }
    }
}

fun isDefaultHome(context: Context): Boolean {
    return try {
        val default = Settings.Secure.getString(context.contentResolver, "launcher_default")
        false
    } catch (e: Exception) { false }
}

fun requestHomeRole(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val rm = context.getSystemService(RoleManager::class.java)
        if (rm.isRoleAvailable(RoleManager.ROLE_HOME)) {
            val intent = rm.createRequestRoleIntent(RoleManager.ROLE_HOME)
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    } else {
        // On Android 9, the system shows a chooser when pressing Home if multiple HOME apps exist
    }
}

fun openSettings(context: Context) {
    val intent = android.content.Intent(android.provider.Settings.ACTION_SETTINGS)
    intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}

fun openRecents(context: Context) {
    // Not directly invocable without special privileges; placeholder
}