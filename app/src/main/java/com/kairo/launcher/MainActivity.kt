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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
        val ctx = /* Local context for actions */
            androidx.compose.ui.platform.LocalContext.current
        val query by vm.query.collectAsState()

        Scaffold(
            // Let Scaffold handle system bars insets; we'll use padding() it provides
            topBar = {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .statusBarsPadding() // keep below clock/status icons
                ) {
                    SearchBar(query) { vm.setQuery(it) }
                    DefaultHomeHint()
                }
            },
            bottomBar = {
                // Lift the dock above the nav bar
                Box(Modifier.navigationBarsPadding()) {
                    RadialDock { slot ->
                        when (slot) {
                            0 -> { /* focus search (future) */ }
                            1 -> { /* app grid is the main view */ }
                            2 -> openRecents(ctx) // placeholder
                            3 -> openSettings(ctx) // opens system Settings for now
                        }
                    }
                }
            }
        ) { paddings ->
            // Apply Scaffold's paddings so content sits between bars
            Box(Modifier.fillMaxSize().padding(paddings)) {
                AppGrid(vm.filtered()) { vm.launch(ctx, it) }
            }
        }
    }
}

@Composable
fun DefaultHomeHint() {
    val ctx = androidx.compose.ui.platform.LocalContext.current
    if (!isDefaultHome(ctx)) {
        Surface(color = MaterialTheme.colorScheme.surfaceVariant) {
            androidx.compose.foundation.layout.Row(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text("Set Kairo as your default Home for the full experience.")
                androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))
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
