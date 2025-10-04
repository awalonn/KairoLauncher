package com.kairo.launcher

import android.app.role.RoleManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.kairo.launcher.ui.components.AppGrid
import com.kairo.launcher.ui.components.RadialDock
import com.kairo.launcher.ui.components.SearchBar
import com.kairo.launcher.ui.theme.KairoTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding

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
            contentWindowInsets = WindowInsets.systemBars,
            topBar = {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()      // ⬅️ keep below the status bar
                ) {
                    SearchBar(query) { vm.setQuery(it) }
                    DefaultHomeHint()
                }
            },
            bottomBar = {
                Box(Modifier.navigationBarsPadding()) { // ⬅️ sit above nav bar
                    RadialDock { slot ->
                        when (slot) {
                            0 -> { /* focus search */ }
                            1 -> { /* app grid is main */ }
                            2 -> openRecents(ctx)
                            3 -> openSettings(ctx)
                        }
                    }
                }
            }
        ) { paddings ->
            Box(Modifier.fillMaxSize().padding(paddings)) {  // ⬅️ use Scaffold paddings
                AppGrid(vm.filtered()) { vm.launch(ctx, it) }
            }
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
