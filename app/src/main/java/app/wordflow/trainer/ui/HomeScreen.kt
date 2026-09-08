package app.wordflow.trainer.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.*
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
import app.wordflow.trainer.ui.theme.*
import kotlin.math.min

@Composable
fun HomeScreen(
    state: UiState,
    onLearn: () -> Unit,
    onLanguage: (String) -> Unit,
) {
    val user = state.user
    val today = min(user.todayCount, user.dailyGoal)
    val langs = user.selectedLanguages

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        Text(VocabRepository.longDate().uppercase(), color = Muted, fontSize = 11.sp, letterSpacing = 1.sp)
        Text("Moin, ${user.name}!", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Fg)
        Spacer(Modifier.height(16.dp))

        // Progress Card
        Card(
            Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Accent),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(100.dp)) {
                    Canvas(Modifier.size(100.dp)) {
                        drawArc(androidx.compose.ui.graphics.Color.White.copy(0.2f), 0f, 360f, false, style = Stroke(8.dp.toPx(), cap = StrokeCap.Round))
                        val sweep = if (user.dailyGoal == 0) 0f else 360f * today / user.dailyGoal
                        drawArc(androidx.compose.ui.graphics.Color.White, -90f, sweep, false, style = Stroke(8.dp.toPx(), cap = StrokeCap.Round))
                    }
                    Text("$today", color = androidx.compose.ui.graphics.Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                }
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = onLearn,
                    colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color.White, contentColor = Accent),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Lektion starten", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        Text("Deine Sprachen", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(Modifier.height(12.dp))

        if (langs.isEmpty()) {
            Box(Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                Text("Keine Sprachen gewählt.", color = Muted)
            }
        } else {
            langs.forEach { lang ->
                val chCount = state.chapters.count { it.lang == lang.id }
                Row(
                    Modifier.fillMaxWidth().padding(bottom = 12.dp).clip(RoundedCornerShape(18.dp))
                        .background(Surface).border(1.dp, Border, RoundedCornerShape(18.dp))
                        .clickable { onLanguage(lang.id) }.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(lang.flag, fontSize = 24.sp)
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text(lang.name, fontWeight = FontWeight.Bold)
                        Text("$chCount Kapitel", color = Muted, fontSize = 13.sp)
                    }
                    Icon(Icons.Outlined.ChevronRight, null, tint = Border)
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
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Outlined.ArrowBack, null) }
            Text(lang.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(16.dp))
        if (chapters.isEmpty()) {
            Text("Noch keine Kapitel für ${lang.name}. Tippe auf Plus um eines zu erstellen.", color = Muted)
        } else {
            chapters.forEach { chapter ->
                Row(
                    Modifier.fillMaxWidth().padding(bottom = 12.dp).clip(RoundedCornerShape(16.dp))
                        .background(Surface).border(1.dp, Border, RoundedCornerShape(16.dp))
                        .clickable { onChapter(chapter.id) }.padding(16.dp)
                ) {
                    Text(chapter.name, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Icon(Icons.Outlined.PlayArrow, null, tint = Accent)
                }
            }
        }
    }
}
