package app.wordflow.trainer.ui

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.wordflow.trainer.CyloneIdAuth
import app.wordflow.trainer.LANGUAGES
import app.wordflow.trainer.UiState
import app.wordflow.trainer.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    state: UiState,
    onDone: (String, List<String>, Int) -> Unit,
    onCyloneLogin: () -> Unit,
) {
    val pager = rememberPagerState(pageCount = { 5 })
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf(setOf("la")) }
    var goal by remember { mutableIntStateOf(24) }
    val context = LocalContext.current

    LaunchedEffect(state.user.cyloneName) {
        if (name.isBlank() && state.user.cyloneName.isNotBlank()) name = state.user.cyloneName
    }

    fun next() {
        if (pager.currentPage < 4) scope.launch { pager.animateScrollToPage(pager.currentPage + 1) }
        else onDone(name.ifBlank { "Nutzer" }, selected.toList(), goal)
    }

    Column(
        Modifier.fillMaxSize().background(Bg).padding(horizontal = 24.dp, vertical = 32.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(5) { i ->
                Box(Modifier.weight(1f).height(4.dp).clip(CircleShape).background(if (i <= pager.currentPage) Accent else Border))
            }
        }
        HorizontalPager(state = pager, modifier = Modifier.weight(1f), userScrollEnabled = false) { page ->
            when (page) {
                0 -> WelcomePage()
                1 -> CyloneLoginPage(state, onCyloneLogin, { next() })
                2 -> NamePage(name) { name = it }
                3 -> LanguageSelectionPage(selected) { selected = it }
                4 -> GoalPage(goal) { goal = it }
            }
        }
        Button(
            onClick = ::next,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Accent),
        ) {
            Text(if (pager.currentPage == 4) "Loslegen" else "Weiter", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun WelcomePage() {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("WordFlow", fontSize = 42.sp, fontWeight = FontWeight.ExtraBold, color = Accent)
        Spacer(Modifier.height(12.dp))
        Text("Vokabeln, die bleiben.", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Fg, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text("Lerne Latein und mehr. Keine Standard-Listen – lade dir deine Buch-Pakete im Store.", color = Muted, textAlign = TextAlign.Center)
    }
}

@Composable
private fun CyloneLoginPage(state: UiState, onLogin: () -> Unit, onSkip: () -> Unit) {
    val context = LocalContext.current
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        Text("KONTO", style = MaterialTheme.typography.labelSmall)
        Spacer(Modifier.height(8.dp))
        Text("Mit Cylone ID anmelden", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(8.dp))
        Text("Verbinde deinen Account für Cloud-Sync und WordFlow PLUS.", color = Muted)
        Spacer(Modifier.height(24.dp))
        if (state.user.isCyloneLinked) {
            Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(AccentSoft).padding(16.dp)) {
                Text("Verbunden als ${state.user.cyloneEmail}", fontWeight = FontWeight.SemiBold, color = Fg)
            }
        } else {
            // Dark grey, no text button for Cylone login as requested
            Button(
                onClick = {
                    onLogin()
                    (context as? Activity)?.let { CyloneIdAuth.startLogin(it) }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyloneGrey),
                enabled = !state.authBusy,
            ) {
                if (state.authBusy) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            }
            TextButton(onClick = onSkip, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Später anmelden", color = Muted)
            }
        }
    }
}

@Composable
private fun NamePage(name: String, onNameChange: (String) -> Unit) {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        Text("PROFIL", style = MaterialTheme.typography.labelSmall)
        Spacer(Modifier.height(8.dp))
        Text("Wie heißt du?", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { if (it.length <= 24) onNameChange(it) },
            placeholder = { Text("Dein Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Accent, unfocusedBorderColor = Border),
        )
    }
}

@Composable
private fun LanguageSelectionPage(selected: Set<String>, onSelect: (Set<String>) -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(vertical = 24.dp)) {
        Text("SPRACHEN", style = MaterialTheme.typography.labelSmall)
        Spacer(Modifier.height(8.dp))
        Text("Was lernst du?", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(16.dp))
        LANGUAGES.forEach { item ->
            val isOn = item.id in selected
            Row(
                Modifier.fillMaxWidth().padding(bottom = 10.dp).clip(RoundedCornerShape(18.dp))
                    .border(if (isOn) 2.dp else 1.dp, if (isOn) Accent else Border, RoundedCornerShape(18.dp))
                    .background(if (isOn) AccentSoft else Surface)
                    .clickable { onSelect(if (isOn) selected - item.id else selected + item.id) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(item.flag, fontSize = 28.sp, modifier = Modifier.padding(end = 12.dp))
                Column(Modifier.weight(1f)) {
                    Text(item.name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    Text(item.level, color = Muted, fontSize = 13.sp)
                }
                if (isOn) Icon(Icons.Default.Check, null, tint = Accent)
            }
        }
    }
}

@Composable
private fun GoalPage(goal: Int, onGoalChange: (Int) -> Unit) {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        Text("TAGESZIEL", style = MaterialTheme.typography.labelSmall)
        Spacer(Modifier.height(8.dp))
        Text("Wörter pro Tag?", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(16.dp))
        listOf(10, 20, 24, 40).forEach { value ->
            val isOn = goal == value
            Box(
                Modifier.fillMaxWidth().padding(bottom = 10.dp).clip(RoundedCornerShape(18.dp))
                    .border(if (isOn) 2.dp else 1.dp, if (isOn) Accent else Border, RoundedCornerShape(18.dp))
                    .background(if (isOn) AccentSoft else Surface)
                    .clickable { onGoalChange(value) }
                    .padding(18.dp),
            ) {
                Text("$value Wörter", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}
