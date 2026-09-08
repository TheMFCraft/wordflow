package app.wordflow.trainer.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoGraph
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Style
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import app.wordflow.trainer.WordFlowViewModel
import app.wordflow.trainer.ui.theme.Accent
import app.wordflow.trainer.ui.theme.Bg
import app.wordflow.trainer.ui.theme.Muted

private object Routes {
    const val Onboarding = "onboarding"
    const val Home = "home"
    const val Learn = "learn"
    const val Scan = "scan"
    const val Stats = "stats"
    const val Pro = "pro"
}

@Composable
fun WordFlowApp(vm: WordFlowViewModel = viewModel()) {
    val state by vm.ui.collectAsStateWithLifecycle()
    if (!state.ready) {
        androidx.compose.foundation.layout.Box(
            Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center,
        ) {
            androidx.compose.material3.CircularProgressIndicator(color = Accent)
        }
        return
    }
    if (!state.user.onboardingDone) {
        OnboardingScreen(onDone = { name, lang, goal -> vm.completeOnboarding(name, lang, goal) })
        return
    }
    val nav = rememberNavController()
    val entry by nav.currentBackStackEntryAsState()
    val route = entry?.destination?.route
    val hideBar = route == Routes.Learn || route == Routes.Pro

    Scaffold(
        containerColor = Bg,
        bottomBar = {
            if (!hideBar && state.user.onboardingDone) {
                NavigationBar(containerColor = androidx.compose.ui.graphics.Color.White) {
                    val items = listOf(
                        Triple(Routes.Home, "Home", Icons.Outlined.Home),
                        Triple(Routes.Learn, "Lernen", Icons.Outlined.Style),
                        Triple(Routes.Scan, "Scan", Icons.Outlined.DocumentScanner),
                        Triple(Routes.Stats, "Statistik", Icons.Outlined.AutoGraph),
                    )
                    items.forEach { (r, label, icon) ->
                        NavigationBarItem(
                            selected = route == r,
                            onClick = {
                                if (r == Routes.Learn) {
                                    vm.startSession()
                                    nav.navigate(Routes.Learn)
                                } else {
                                    nav.navigate(r) {
                                        popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Accent,
                                selectedTextColor = Accent,
                                unselectedIconColor = Muted,
                                unselectedTextColor = Muted,
                            ),
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = Routes.Home,
            modifier = Modifier.padding(padding).fillMaxSize(),
        ) {
            composable(Routes.Home) {
                HomeScreen(
                    state = state,
                    onLearn = { vm.startSession(); nav.navigate(Routes.Learn) },
                    onLang = { id ->
                        val info = app.wordflow.trainer.LANGUAGES.first { it.id == id }
                        if (!info.free && !state.user.isPro) nav.navigate(Routes.Pro)
                        else {
                            vm.selectLang(id)
                            vm.startSession()
                            nav.navigate(Routes.Learn)
                        }
                    },
                    onScan = { nav.navigate(Routes.Scan) },
                    onPro = { nav.navigate(Routes.Pro) },
                )
            }
            composable(Routes.Learn) {
                LearnScreen(
                    state = state,
                    onFlip = vm::flip,
                    onGrade = vm::grade,
                    onSpeak = { word, lang -> vm.speak(word, lang) },
                    onClose = { vm.endSession(); nav.popBackStack() },
                    onAgain = { vm.startSession() },
                    onHome = { vm.endSession(); nav.navigate(Routes.Home) { popUpTo(Routes.Home) { inclusive = true } } },
                )
            }
            composable(Routes.Scan) {
                ScanScreen(
                    state = state,
                    onImport = { lang, pairs -> vm.importPairs(lang, pairs) },
                    recognize = { bmp, ja -> vm.recognizeText(bmp, ja) },
                )
            }
            composable(Routes.Stats) { StatsScreen(state) }
            composable(Routes.Pro) {
                ProScreen(state, onActivate = { vm.setPro(true) }, onCancel = { vm.setPro(false) }, onClose = { nav.popBackStack() })
            }
        }
    }
}
