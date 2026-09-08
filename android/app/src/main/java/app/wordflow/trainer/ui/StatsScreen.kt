package app.wordflow.trainer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.wordflow.trainer.UiState
import app.wordflow.trainer.VocabRepository
import app.wordflow.trainer.ui.theme.Accent
import app.wordflow.trainer.ui.theme.Accent2
import app.wordflow.trainer.ui.theme.Border
import app.wordflow.trainer.ui.theme.Fg
import app.wordflow.trainer.ui.theme.Muted
import app.wordflow.trainer.ui.theme.Success
import app.wordflow.trainer.ui.theme.Surface

@Composable
fun StatsScreen(state: UiState) {
    val user = state.user
    val acc = if (user.totalAttempts == 0) 0 else user.totalCorrect * 100 / user.totalAttempts
    val hours = user.totalSeconds / 3600.0
    val timeLabel = if (hours < 1) "${user.totalSeconds / 60}m" else "${"%.1f".format(hours)}h"
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        Text("DEINE FORTSCHRITTE", color = Muted, fontSize = 11.sp, letterSpacing = 1.sp)
        Text("Statistik", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard("${state.learnedTotal}", "Wörter gelernt", Modifier.weight(1f))
            StatCard(timeLabel, "Lernzeit", Modifier.weight(1f))
        }
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard("$acc%", "Genauigkeit", Modifier.weight(1f), Success)
            StatCard("${user.streak}", "Streak", Modifier.weight(1f), Accent)
            StatCard("${state.learnedByLang.count { it.value.second > 0 }}", "Sprachen", Modifier.weight(1f), Accent2)
        }
        Spacer(Modifier.height(20.dp))
        Text("Letzte Aktivität", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        Spacer(Modifier.height(8.dp))
        Column(Modifier.clip(RoundedCornerShape(18.dp)).background(Surface).border(1.dp, Border, RoundedCornerShape(18.dp)).padding(16.dp).fillMaxWidth()) {
            if (state.activity.isEmpty()) {
                Text("Noch keine Aktivität. Starte deine erste Lektion oder scanne eine Liste.", color = Muted)
            } else {
                state.activity.take(8).forEach { item ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(Modifier.weight(1f)) {
                            Text(item.title, fontWeight = FontWeight.Medium)
                            Text("${item.words} Wörter · ${item.accuracy}% richtig", color = Muted, fontSize = 12.sp)
                        }
                        Text(VocabRepository.formatWhen(item.date), color = Muted, fontSize = 12.sp)
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun StatCard(value: String, label: String, modifier: Modifier, color: androidx.compose.ui.graphics.Color = Fg) {
    Column(modifier.clip(RoundedCornerShape(18.dp)).background(Surface).border(1.dp, Border, RoundedCornerShape(18.dp)).padding(16.dp)) {
        Text(value, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = color)
        Text(label, color = Muted, fontSize = 12.sp)
    }
}
