package app.wordflow.trainer.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import app.wordflow.trainer.WordFlowViewModel
import app.wordflow.trainer.ui.theme.*

private object Routes {
    const val Home = "home"
    const val Store = "store"
    const val Stats = "stats"
    const val Scan = "scan"
    const val Language = "language/{id}"
    const val Learn = "learn"
    const val Settings = "settings"
    const val Onboarding = "onboarding"
}

@Composable
fun WordFlowApp(vm: WordFlowViewModel = viewModel()) {
    val state by vm.ui.collectAsStateWithLifecycle()
    val nav = rememberNavController()
    val entry by nav.currentBackStackEntryAsState()
    val route = entry?.destination?.route
    
    if (!state.ready) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Accent)
        }
        return
    }

    if (!state.user.onboardingDone) {
        OnboardingScreen(
            state = state,
            onDone = { name, langs, goal -> vm.completeOnboarding(name, langs, goal) },
            onCyloneLogin = vm::markAuthBusy
        )
        return
    }

    val hideBar = route == Routes.Learn
    var plusOpen by remember { mutableStateOf(false) }
    
    val barSelected = when {
        route == Routes.Settings -> "settings"
        route == Routes.Store -> "store"
        route == Routes.Stats -> "stats"
        else -> "home"
    }

    Scaffold(
        containerColor = Bg,
        bottomBar = {
            if (!hideBar) {
                WordFlowBottomBar(
                    selected = barSelected,
                    onHome = { nav.navigate(Routes.Home) { popUpTo(nav.graph.findStartDestination().id) { saveState = true }; launchSingleTop = true; restoreState = true } },
                    onStore = { nav.navigate(Routes.Store) { popUpTo(nav.graph.findStartDestination().id) { saveState = true }; launchSingleTop = true; restoreState = true } },
                    onPlus = { plusOpen = true },
                    onStats = { nav.navigate(Routes.Stats) { popUpTo(nav.graph.findStartDestination().id) { saveState = true }; launchSingleTop = true; restoreState = true } },
                    onSettings = { nav.navigate(Routes.Settings) { popUpTo(nav.graph.findStartDestination().id) { saveState = true }; launchSingleTop = true; restoreState = true } }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = Routes.Home,
            modifier = Modifier.padding(padding).fillMaxSize()
        ) {
            composable(Routes.Home) {
                HomeScreen(
                    state = state,
                    onLearn = {
                        vm.startSession(langId = state.user.selectedLang)
                        nav.navigate(Routes.Learn)
                    },
                    onLanguage = { id ->
                        vm.selectLang(id)
                        nav.navigate("language/$id")
                    }
                )
            }
            composable(Routes.Store) { StoreScreen(state) }
            composable(Routes.Stats) { StatsScreen(state) }
            composable(Routes.Scan) {
                ScanScreen(
                    state = state,
                    onBack = { nav.popBackStack() },
                    onImport = vm::importPairs,
                    recognize = vm::recognizeText
                )
            }
            composable(
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { backStack ->
                val id = backStack.arguments?.getString("id").orEmpty()
                LanguageScreen(
                    state = state,
                    langId = id,
                    onBack = { nav.popBackStack() },
                    onChapter = { chId ->
                        vm.startSession(chapterId = chId, langId = id)
                        nav.navigate(Routes.Learn)
                    }
                )
            }
            composable(Routes.Learn) {
                LearnScreen(
                    state = state,
                    onFlip = vm::flip,
                    onGrade = vm::grade,
                    onSpeak = { w, l -> vm.speak(w, l) },
                    onClose = { vm.endSession(); nav.popBackStack() },
                    onAgain = { vm.startSession(state.session?.chapterId, state.session?.lang?.id) },
                    onHome = { vm.endSession(); nav.popBackStack(Routes.Home, false) }
                )
            }
            composable(Routes.Settings) {
                SettingsScreen(
                    state = state,
                    onName = vm::setName,
                    onGoal = vm::setDailyGoal,
                    onPlus = vm::setPlus,
                    onCyloneLogin = vm::markAuthBusy,
                    onCyloneLogout = vm::unlinkCylone
                )
            }
        }
    }

    if (plusOpen) {
        AddContentSheet(
            state = state,
            onDismiss = { plusOpen = false },
            onAddLanguage = vm::addLanguage,
            onAddChapter = vm::createChapter,
            onAddWord = vm::addWord,
            onScan = { plusOpen = false; nav.navigate(Routes.Scan) }
        )
    }
}
