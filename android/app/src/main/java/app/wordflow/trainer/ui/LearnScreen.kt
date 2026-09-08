package app.wordflow.trainer.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.wordflow.trainer.LanguageInfo
import app.wordflow.trainer.UiState
import app.wordflow.trainer.WordEntity
import app.wordflow.trainer.ui.theme.Accent
import app.wordflow.trainer.ui.theme.AccentSoft
import app.wordflow.trainer.ui.theme.Bg
import app.wordflow.trainer.ui.theme.Error
import app.wordflow.trainer.ui.theme.Fg
import app.wordflow.trainer.ui.theme.Muted
import app.wordflow.trainer.ui.theme.Success
import app.wordflow.trainer.ui.theme.Surface

@Composable
fun LearnScreen(
    state: UiState,
    onFlip: () -> Unit,
    onGrade: (Boolean) -> Unit,
    onSpeak: (WordEntity, LanguageInfo) -> Unit,
    onClose: () -> Unit,
    onAgain: () -> Unit,
    onHome: () -> Unit,
) {
    val session = state.session
    Column(Modifier.fillMaxSize().background(Bg).padding(20.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text((session?.let { "${it.lang.name} · ${it.chapterName}" } ?: "Karteikarten").uppercase(), color = Muted, fontSize = 11.sp, letterSpacing = 1.sp)
                Text(if (session?.done == true) "Lektion fertig" else "Karteikarten", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
            }
            IconButton(onClick = onClose) { Icon(Icons.Outlined.Close, contentDescription = "Beenden") }
        }
        if (session == null) {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Keine Karten in dieser Sprache.", color = Muted)
                Spacer(Modifier.height(16.dp))
                Button(onClick = onHome, colors = ButtonDefaults.buttonColors(Accent)) { Text("Zurück") }
            }
            return
        }
        if (session.done) {
            Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Accent).padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("GEWUSST", color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.72f))
                Text("${session.known}/${session.results.size}", fontSize = 56.sp, fontWeight = FontWeight.ExtraBold, color = androidx.compose.ui.graphics.Color.White)
                val pct = if (session.results.isEmpty()) 0 else session.known * 100 / session.results.size
                Text("$pct% in dieser Runde", color = androidx.compose.ui.graphics.Color.White)
            }
            Spacer(Modifier.height(16.dp))
            Button(onClick = onAgain, modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(Accent), shape = RoundedCornerShape(16.dp)) {
                Text("Nochmal üben", fontWeight = FontWeight.Bold)
            }
            Button(onClick = onHome, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color.Transparent, contentColor = Accent)) {
                Text("Zurück zur Übersicht")
            }
            return
        }
        val word = session.current ?: return
        val total = session.queue.size
        val current = session.index + 1
        LinearProgressIndicator(
            progress = { session.index.toFloat() / total.coerceAtLeast(1) },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
            color = Accent,
            trackColor = app.wordflow.trainer.ui.theme.Border,
        )
        Text("$current / $total", color = Muted, modifier = Modifier.align(Alignment.End).padding(top = 6.dp))
        Spacer(Modifier.height(16.dp))
        val rotation by animateFloatAsState(if (session.flipped) 180f else 0f, label = "flip")
        Box(
            Modifier.fillMaxWidth().height(320.dp).graphicsLayer { rotationY = rotation; cameraDistance = 16f * density }
                .clip(RoundedCornerShape(24.dp))
                .background(if (rotation > 90f) Accent else Surface)
                .clickable(onClick = onFlip)
                .padding(28.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (rotation <= 90f) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = { onSpeak(word, session.lang) }, modifier = Modifier.align(Alignment.End)) {
                        Icon(Icons.Outlined.VolumeUp, contentDescription = "Aussprache")
                    }
                    Box(Modifier.clip(CircleShape).background(AccentSoft).padding(horizontal = 10.dp, vertical = 4.dp)) {
                        Text(word.pos.uppercase(), color = Accent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(word.word, fontSize = 34.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
                    if (word.phonetic.isNotBlank()) Text(word.phonetic, color = Muted, modifier = Modifier.padding(top = 6.dp))
                    if (word.example.isNotBlank()) Text("„${word.example}“", color = Muted, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 12.dp))
                    Text("Tippen zum Umdrehen", color = Muted, fontSize = 11.sp, modifier = Modifier.padding(top = 20.dp))
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.graphicsLayer { rotationY = 180f }) {
                    Box(Modifier.clip(CircleShape).background(androidx.compose.ui.graphics.Color.White.copy(alpha = 0.2f)).padding(horizontal = 10.dp, vertical = 4.dp)) {
                        Text("ÜBERSETZUNG", color = androidx.compose.ui.graphics.Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(word.translation, fontSize = 34.sp, fontWeight = FontWeight.ExtraBold, color = androidx.compose.ui.graphics.Color.White, textAlign = TextAlign.Center)
                    val meta = listOf(word.pos, word.gender).filter { it.isNotBlank() }.joinToString(" · ")
                    if (meta.isNotBlank()) Text(meta, color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.75f), modifier = Modifier.padding(top = 6.dp))
                    if (word.exampleDe.isNotBlank()) Text("„${word.exampleDe}“", color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.85f), textAlign = TextAlign.Center, modifier = Modifier.padding(top = 12.dp))
                    Text("Tippen zum Umdrehen", color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.5f), fontSize = 11.sp, modifier = Modifier.padding(top = 20.dp))
                }
            }
        }
        Spacer(Modifier.height(20.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { onGrade(false) }, modifier = Modifier.weight(1f).height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = Error.copy(alpha = 0.12f), contentColor = Error), shape = RoundedCornerShape(16.dp)) {
                Icon(Icons.Outlined.Close, null); Spacer(Modifier.width(6.dp)); Text("Nochmal")
            }
            Button(onClick = { onGrade(true) }, modifier = Modifier.weight(1f).height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = Success.copy(alpha = 0.14f), contentColor = Success), shape = RoundedCornerShape(16.dp)) {
                Icon(Icons.Outlined.Check, null); Spacer(Modifier.width(6.dp)); Text("Gewusst")
            }
        }
    }
}
