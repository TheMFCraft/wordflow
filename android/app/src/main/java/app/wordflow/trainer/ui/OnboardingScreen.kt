package app.wordflow.trainer.ui

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.wordflow.trainer.CyloneIdAuth
import app.wordflow.trainer.LANGUAGES
import app.wordflow.trainer.UiState
import app.wordflow.trainer.ui.theme.Accent
import app.wordflow.trainer.ui.theme.AccentSoft
import app.wordflow.trainer.ui.theme.Bg
import app.wordflow.trainer.ui.theme.Border
import app.wordflow.trainer.ui.theme.Fg
import app.wordflow.trainer.ui.theme.Muted
import app.wordflow.trainer.ui.theme.Surface
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
    var selected by remember { mutableStateOf(setOf("es")) }
    var goal by remember { mutableIntStateOf(24) }
    val context = LocalContext.current

    LaunchedEffect(state.user.cyloneName, state.user.name) {
        val incoming = state.user.name.ifBlank { state.user.cyloneName }
        if (name.isBlank() && incoming.isNotBlank()) name = incoming.take(24)
    }

    LaunchedEffect(state.user.isCyloneLinked) {
        if (state.user.isCyloneLinked && pager.currentPage == 1) {
            pager.animateScrollToPage(2)
        }
    }

    fun next() {
        when (pager.currentPage) {
            2 -> if (name.isBlank() && !state.user.isCyloneLinked) return
            3 -> if (selected.isEmpty()) return
        }
        if (pager.currentPage < 4) scope.launch { pager.animateScrollToPage(pager.currentPage + 1) }
        else onDone(name.ifBlank { state.user.cyloneName.ifBlank { "Anna" } }, selected.toList(), goal)
    }

    val canContinue = when (pager.currentPage) {
        1 -> true
        2 -> name.isNotBlank() || state.user.isCyloneLinked
        3 -> selected.isNotEmpty()
        else -> true
    }

    Column(
        Modifier.fillMaxSize().background(Bg).padding(horizontal = 24.dp, vertical = 32.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(5) { i ->
                Box(
                    Modifier.weight(1f).height(4.dp).clip(CircleShape)
                        .background(if (i <= pager.currentPage) Accent else Border),
                )
            }
        }
        HorizontalPager(state = pager, modifier = Modifier.weight(1f), userScrollEnabled = false) { page ->
            when (page) {
                0 -> Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("WordFlow", fontSize = 42.sp, fontWeight = FontWeight.ExtraBold, color = Accent)
                    Spacer(Modifier.height(12.dp))
                    Text("Vokabeln, die bleiben.", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Fg, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(8.dp))
                    Text("Sprachen wählen, Kapitel anlegen und mit Karteikarten lernen — warm, klar, alltagstauglich.", color = Muted, textAlign = TextAlign.Center)
                }
                1 -> Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
                    Text("KONTO", style = androidx.compose.material3.MaterialTheme.typography.labelSmall)
                    Spacer(Modifier.height(8.dp))
                    Text("Mit Cylone ID anmelden", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.height(8.dp))
                    Text("Ein Konto für WordFlow, Cloud-Sync und WordFlow PLUS. Du kannst dich auch später in den Einstellungen verbinden.", color = Muted)
                    Spacer(Modifier.height(20.dp))
                    if (state.user.isCyloneLinked) {
                        Box(
                            Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(AccentSoft).padding(16.dp),
                        ) {
                            Text("Verbunden als ${state.user.cyloneEmail.ifBlank { state.user.cyloneName }}", fontWeight = FontWeight.SemiBold, color = Fg)
                        }
                    } else {
                        Button(
                            onClick = {
                                onCyloneLogin()
                                (context as? Activity)?.let { CyloneIdAuth.startLogin(it) }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Fg),
                            enabled = !state.authBusy,
                        ) {
                            if (state.authBusy) CircularProgressIndicator(color = androidx.compose.ui.graphics.Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                            else Text("Mit Cylone ID anmelden", fontWeight = FontWeight.Bold)
                        }
                        if (!state.cyloneConfigured) {
                            Text(
                                "Hinweis: Client-ID noch nicht hinterlegt. Anmeldung wird aktiv, sobald WordFlow bei Cylone ID registriert ist.",
                                color = Muted,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 10.dp),
                            )
                        }
                        state.authMessage?.let {
                            Text(it, color = if (state.user.isCyloneLinked) Accent else app.wordflow.trainer.ui.theme.Error, modifier = Modifier.padding(top = 10.dp), fontSize = 13.sp)
                        }
                    }
                    TextButton(onClick = ::next, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Text(if (state.user.isCyloneLinked) "Weiter" else "Später anmelden", color = Muted)
                    }
                }
                2 -> Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
                    Text("WIE SOLLEN WIR DICH NENNEN?", style = androidx.compose.material3.MaterialTheme.typography.labelSmall)
                    Spacer(Modifier.height(8.dp))
                    Text("Dein Name", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { if (it.length <= 24) name = it },
                        placeholder = { Text("z. B. Anna") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Accent, unfocusedBorderColor = Border, focusedContainerColor = Surface, unfocusedContainerColor = Surface),
                    )
                }
                3 -> Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(vertical = 24.dp)) {
                    Text("SPRACHEN", style = androidx.compose.material3.MaterialTheme.typography.labelSmall)
                    Spacer(Modifier.height(8.dp))
                    Text("Was willst du lernen?", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.height(6.dp))
                    Text("Wähle eine oder mehrere Sprachen. Im Home siehst du danach nur diese.", color = Muted)
                    Spacer(Modifier.height(16.dp))
                    LANGUAGES.forEach { item ->
                        val isOn = item.id in selected
                        Row(
                            Modifier.fillMaxWidth().padding(bottom = 10.dp).clip(RoundedCornerShape(18.dp))
                                .border(if (isOn) 2.dp else 1.dp, if (isOn) Accent else Border, RoundedCornerShape(18.dp))
                                .background(if (isOn) AccentSoft else Surface)
                                .clickable {
                                    selected = if (isOn) selected - item.id else selected + item.id
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(item.flag, fontSize = 28.sp, modifier = Modifier.padding(end = 12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(item.name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                                Text("${item.pack} · ${item.level}", color = Muted, fontSize = 13.sp)
                            }
                            Text(if (isOn) "✓" else "", color = Accent, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                else -> Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
                    Text("TAGESZIEL", style = androidx.compose.material3.MaterialTheme.typography.labelSmall)
                    Spacer(Modifier.height(8.dp))
                    Text("Wie viele Wörter am Tag?", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.height(16.dp))
                    listOf(10, 20, 24, 40).forEach { value ->
                        val isOn = goal == value
                        Box(
                            Modifier.fillMaxWidth().padding(bottom = 10.dp).clip(RoundedCornerShape(18.dp))
                                .border(if (isOn) 2.dp else 1.dp, if (isOn) Accent else Border, RoundedCornerShape(18.dp))
                                .background(if (isOn) AccentSoft else Surface)
                                .clickable { goal = value }
                                .padding(18.dp),
                        ) {
                            Text("$value Wörter", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
        Button(
            onClick = ::next,
            enabled = canContinue,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Accent),
        ) {
            Text(
                when (pager.currentPage) {
                    4 -> "Loslegen"
                    1 -> if (state.user.isCyloneLinked) "Weiter" else "Später anmelden"
                    else -> "Weiter"
                },
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )
        }
        Spacer(Modifier.height(12.dp))
    }
}
