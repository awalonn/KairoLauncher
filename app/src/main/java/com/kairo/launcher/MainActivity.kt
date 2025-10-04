
package com.kairo.launcher

import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
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
        vm.observePrefs(this)
        setContent { KairoApp(vm) }
    }
}

@Composable
fun KairoApp(vm: LauncherViewModel) {
    KairoTheme {
        val ctx = LocalContext.current
        val query by vm.query.collectAsState()
        val grid by vm.gridSize.collectAsState()

        Scaffold(
            topBar = {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                ) {
                    SearchBar(query) { vm.setQuery(it) }
                    DefaultHomeHint()
                }
            },
            bottomBar = {
                Box(Modifier.navigationBarsPadding()) {
                    RadialDock { slot ->
                        when (slot) {
                            0 -> { /* focus search (future) */ }
                            1 -> { /* app grid is the main view */ }
                            2 -> openRecents(ctx) // placeholder
                            3 -> ctx.startActivity(
                                Intent(ctx, SettingsActivity::class.java)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            )
                        }
                    }
                }
            }
        ) { paddings ->
            Box(Modifier.fillMaxSize().padding(paddings)) {
                AppGrid(
                    apps = vm.filtered(),
                    gridMin = grid,
                    onOpen = { app -> vm.launch(ctx, app) }
                )
            }
        }
    }
}

@Composable
fun DefaultHomeHint() {
    val ctx = LocalContext.current
    if (!isDefaultHome(ctx)) {
        Surface(color = MaterialTheme.colorScheme.surfaceVariant) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text("Set Kairo as your default Home for the full experience.")
                Spacer(Modifier.weight(1f))
                TextButton(onClick = { requestHomeRole(ctx) }) { Text("Set Default") }
            }
        }
    }
}

/** Accurate default-home check for Android 9–12. */
fun isDefaultHome(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val rm = context.getSystemService(RoleManager::class.java)
        rm.isRoleAvailable(RoleManager.ROLE_HOME) && rm.isRoleHeld(RoleManager.ROLE_HOME)
    } else {
        val homeIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
        val res = context.packageManager.resolveActivity(homeIntent, 0)
        res?.activityInfo?.packageName == context.packageName
    }
}

fun requestHomeRole(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val rm = context.getSystemService(RoleManager::class.java)
        if (rm.isRoleAvailable(RoleManager.ROLE_HOME)) {
            val intent = rm.createRequestRoleIntent(RoleManager.ROLE_HOME)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    } else {
        // On Android 9, pressing Home will show the chooser if multiple HOME apps exist.
    }
}

fun openSettings(context: Context) {
    val intent = Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}

fun openRecents(@Suppress("UNUSED_PARAMETER") context: Context) {
    // Recents isn't directly invocable without special privileges; placeholder.
}
