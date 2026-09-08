package app.wordflow.trainer.ui

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.wordflow.trainer.UiState
import app.wordflow.trainer.VocabRepository
import app.wordflow.trainer.languageById
import app.wordflow.trainer.ui.theme.Accent
import app.wordflow.trainer.ui.theme.Accent2
import app.wordflow.trainer.ui.theme.AccentSoft
import app.wordflow.trainer.ui.theme.Border
import app.wordflow.trainer.ui.theme.Fg
import app.wordflow.trainer.ui.theme.Muted
import app.wordflow.trainer.ui.theme.Success
import app.wordflow.trainer.ui.theme.Surface
import kotlin.math.min

@Composable
fun HomeScreen(
    state: UiState,
    onLearn: () -> Unit,
    onLanguage: (String) -> Unit,
) {
    val user = state.user
    val today = min(user.todayCount, user.dailyGoal)
    val remaining = (user.dailyGoal - user.todayCount).coerceAtLeast(0)
    val acc = if (user.totalAttempts == 0) 0 else (user.totalCorrect * 100 / user.totalAttempts)
    val langs = user.selectedLanguages

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        Text(VocabRepository.longDate().uppercase(), color = Muted, fontSize = 11.sp, letterSpacing = 1.sp)
        Text("Moin, ${user.name.ifBlank { "du" }}!", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Fg)
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.clip(CircleShape).background(Accent2.copy(alpha = 0.25f)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                Text("🔥 ${user.streak} ${if (user.streak == 1) "Tag" else "Tage"} streak!", fontWeight = FontWeight.Bold, color = Fg, fontSize = 13.sp)
            }
            if (user.isPlus) {
                Box(Modifier.clip(CircleShape).background(AccentSoft).padding(horizontal = 10.dp, vertical = 4.dp)) {
                    Text("PLUS", color = Accent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Column(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Accent).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("HEUTIGER FORTSCHRITT", color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.72f), fontSize = 12.sp)
            Spacer(Modifier.height(12.dp))
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(120.dp)) {
                Canvas(Modifier.size(120.dp)) {
                    drawArc(androidx.compose.ui.graphics.Color.White.copy(alpha = 0.28f), 0f, 360f, false, style = Stroke(10.dp.toPx(), cap = StrokeCap.Round))
                    val sweep = if (user.dailyGoal == 0) 0f else 360f * today / user.dailyGoal
                    drawArc(androidx.compose.ui.graphics.Color.White, -90f, sweep, false, style = Stroke(10.dp.toPx(), cap = StrokeCap.Round))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$today", color = androidx.compose.ui.graphics.Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                    Text("/ ${user.dailyGoal}", color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                }
            }
            Text(if (remaining > 0) "Noch $remaining Wörter bis zum Ziel!" else "Tagesziel erreicht. Stark!", color = androidx.compose.ui.graphics.Color.White, modifier = Modifier.padding(top = 8.dp))
            Spacer(Modifier.height(16.dp))
            Button(onClick = onLearn, colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color.White, contentColor = Accent), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().height(52.dp)) {
                Text(if (user.todayCount > 0) "Weiterlernen" else "Heute lernen", fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MiniStat(state.learnedTotal.toString(), "Gelernt", Accent, Modifier.weight(1f))
            MiniStat("$acc%", "Genauigkeit", Accent2, Modifier.weight(1f))
            MiniStat("${langs.size}", "Sprachen", Success, Modifier.weight(1f))
        }
        Spacer(Modifier.height(20.dp))
        Text("Deine Sprachen", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        Spacer(Modifier.height(10.dp))
        if (langs.isEmpty()) {
            Text("Noch keine Sprache gewählt. Tippe auf Plus, um eine hinzuzufügen.", color = Muted)
        } else {
            langs.forEach { item ->
                val counts = state.learnedByLang[item.id] ?: (0 to 0)
                val chapterCount = state.chapters.count { it.lang == item.id }
                Row(
                    Modifier.fillMaxWidth().padding(bottom = 10.dp).clip(RoundedCornerShape(18.dp))
                        .border(1.dp, if (user.selectedLang == item.id) Accent else Border, RoundedCornerShape(18.dp))
                        .background(Surface)
                        .clickable { onLanguage(item.id) }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(item.flag, fontSize = 26.sp, modifier = Modifier.padding(end = 12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(item.name, fontWeight = FontWeight.SemiBold)
                        Text("$chapterCount Kapitel · ${counts.second} Wörter", color = Muted, fontSize = 13.sp)
                    }
                    Text("›", color = Muted, fontSize = 20.sp)
                }
            }
        }
        Spacer(Modifier.height(40.dp))
    }
}

@Composable
fun LanguageScreen(
    state: UiState,
    langId: String,
    onBack: () -> Unit,
    onChapter: (String) -> Unit,
) {
    val lang = languageById(langId)
    val chapters = state.chapters.filter { it.lang == langId }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Outlined.ArrowBack, contentDescription = "Zurück") }
            Column {
                Text(lang.name.uppercase(), color = Muted, fontSize = 11.sp, letterSpacing = 1.sp)
                Text("Kapitel", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
        Spacer(Modifier.height(8.dp))
        Text("Tippe ein Kapitel, um die Vokabeln zu lernen. Neue Kapitel legst du über das Plus an.", color = Muted)
        Spacer(Modifier.height(16.dp))
        if (chapters.isEmpty()) {
            Text("Noch keine Kapitel. Öffne das Plus und lege dein erstes Kapitel an.", color = Muted)
        } else {
            chapters.forEach { chapter ->
                val words = state.words.count { it.chapterId == chapter.id }
                val learned = state.words.count { it.chapterId == chapter.id && it.box >= 2 }
                Row(
                    Modifier.fillMaxWidth().padding(bottom = 10.dp).clip(RoundedCornerShape(18.dp))
                        .border(1.dp, Border, RoundedCornerShape(18.dp))
                        .background(Surface)
                        .clickable { onChapter(chapter.id) }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(chapter.name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        Text("$learned/$words gelernt", color = Muted, fontSize = 13.sp)
                    }
                    Text("Lernen ›", color = Accent, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun MiniStat(value: String, label: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier) {
    Column(modifier.clip(RoundedCornerShape(18.dp)).background(Surface).border(1.dp, Border, RoundedCornerShape(18.dp)).padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
        Text(label, color = Muted, fontSize = 12.sp)
    }
}
