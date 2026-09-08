package app.wordflow.trainer.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import app.wordflow.trainer.WordFlowViewModel
import app.wordflow.trainer.ui.theme.Accent
import app.wordflow.trainer.ui.theme.Bg

private object Routes {
    const val Home = "home"
    const val Language = "language/{id}"
    const val Learn = "learn"
    const val Scan = "scan"
    const val Settings = "settings"
}

@Composable
fun WordFlowApp(vm: WordFlowViewModel = viewModel()) {
    val state by vm.ui.collectAsStateWithLifecycle()
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
            onCyloneLogin = vm::markAuthBusy,
        )
        return
    }

    val nav = rememberNavController()
    val entry by nav.currentBackStackEntryAsState()
    val route = entry?.destination?.route
    val hideBar = route == Routes.Learn
    var plusOpen by remember { mutableStateOf(false) }
    val barSelected = when {
        route == Routes.Settings -> "settings"
        else -> "home"
    }

    fun goTab(target: String) {
        nav.navigate(target) {
            popUpTo(nav.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        containerColor = Bg,
        bottomBar = {
            if (!hideBar) {
                WordFlowBottomBar(
                    selected = barSelected,
                    onHome = { goTab(Routes.Home) },
                    onPlus = { plusOpen = true },
                    onSettings = { goTab(Routes.Settings) },
                )
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
                    onLearn = {
                        vm.startSession(langId = state.user.selectedLang)
                        nav.navigate(Routes.Learn)
                    },
                    onLanguage = { id ->
                        vm.selectLang(id)
                        nav.navigate("language/$id")
                    },
                )
            }
            composable(
                Routes.Language,
                arguments = listOf(navArgument("id") { type = NavType.StringType }),
            ) { backStack ->
                val id = backStack.arguments?.getString("id").orEmpty()
                LanguageScreen(
                    state = state,
                    langId = id,
                    onBack = { nav.popBackStack() },
                    onChapter = { chapterId ->
                        vm.startSession(chapterId = chapterId, langId = id)
                        nav.navigate(Routes.Learn)
                    },
                )
            }
            composable(Routes.Learn) {
                LearnScreen(
                    state = state,
                    onFlip = vm::flip,
                    onGrade = vm::grade,
                    onSpeak = { word, lang -> vm.speak(word, lang) },
                    onClose = { vm.endSession(); nav.popBackStack() },
                    onAgain = { vm.startSession(state.session?.chapterId?.ifBlank { null }, state.session?.lang?.id) },
                    onHome = {
                        vm.endSession()
                        nav.navigate(Routes.Home) { popUpTo(Routes.Home) { inclusive = true } }
                    },
                )
            }
            composable(Routes.Scan) {
                ScanScreen(
                    state = state,
                    onImport = { lang, chapterId, pairs -> vm.importPairs(lang, chapterId, pairs) },
                    recognize = { bmp, ja -> vm.recognizeText(bmp, ja) },
                )
            }
            composable(Routes.Settings) {
                SettingsScreen(
                    state = state,
                    onName = vm::setName,
                    onGoal = vm::setDailyGoal,
                    onPlus = vm::setPlus,
                    onCyloneLogin = vm::markAuthBusy,
                    onCyloneLogout = vm::unlinkCylone,
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
            onScan = {
                plusOpen = false
                nav.navigate(Routes.Scan)
            },
        )
    }
}
