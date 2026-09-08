package app.wordflow.trainer.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.wordflow.trainer.LanguageInfo
import app.wordflow.trainer.SessionState
import app.wordflow.trainer.UiState
import app.wordflow.trainer.WordEntity
import app.wordflow.trainer.ui.theme.*

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
                Text((session?.let { "${it.lang.name} · ${it.chapterName}" } ?: "KARTEIKARTEN").uppercase(), color = Muted, fontSize = 11.sp, letterSpacing = 1.sp)
                Text(if (session?.done == true) "Fertig!" else "Lernen", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
            }
            IconButton(onClick = onClose) { Icon(Icons.Outlined.Close, null) }
        }
        
        if (session == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Keine Vokabeln im Kapitel.", color = Muted)
            }
            return
        }

        if (session.done) {
            DoneView(session, onAgain, onHome)
            return
        }

        val word = session.current ?: return
        Spacer(Modifier.height(24.dp))
        
        val rotation by animateFloatAsState(if (session.flipped) 180f else 0f, label = "flip")
        
        Box(
            Modifier.fillMaxWidth().height(360.dp).graphicsLayer { rotationY = rotation; cameraDistance = 12f * density }
                .clip(RoundedCornerShape(32.dp))
                .background(if (rotation > 90f) Accent else Surface)
                .clickable(onClick = onFlip)
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            if (rotation <= 90f) {
                FrontSide(word, session.lang, onSpeak)
            } else {
                BackSide(word)
            }
        }

        Spacer(Modifier.weight(1f))
        
        Row(Modifier.fillMaxWidth().padding(bottom = 24.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = { onGrade(false) },
                modifier = Modifier.weight(1f).height(64.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Error.copy(0.15f), contentColor = Error),
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(Icons.Outlined.Close, null)
                Spacer(Modifier.width(8.dp))
                Text("Nochmal", fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = { onGrade(true) },
                modifier = Modifier.weight(1f).height(64.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Success.copy(0.15f), contentColor = Success),
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(Icons.Outlined.Check, null)
                Spacer(Modifier.width(8.dp))
                Text("Gewusst", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun FrontSide(word: WordEntity, lang: LanguageInfo, onSpeak: (WordEntity, LanguageInfo) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = { onSpeak(word, lang) }, modifier = Modifier.align(Alignment.End)) {
            Icon(Icons.Outlined.VolumeUp, null, tint = Accent)
        }
        Text(word.word, fontSize = 38.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
        if (word.phonetic.isNotBlank()) Text(word.phonetic, color = Muted, modifier = Modifier.padding(top = 8.dp))
        Spacer(Modifier.height(40.dp))
        Text("Tippen zum Umdrehen", color = Muted, fontSize = 12.sp)
    }
}

@Composable
private fun BackSide(word: WordEntity) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.graphicsLayer { rotationY = 180f }) {
        Text(word.translation, fontSize = 38.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, textAlign = TextAlign.Center)
        if (word.example.isNotBlank()) {
            Spacer(Modifier.height(24.dp))
            Text("„${word.example}“", color = Color.White.copy(0.9f), textAlign = TextAlign.Center, fontSize = 16.sp)
        }
    }
}

@Composable
private fun DoneView(session: SessionState, onAgain: () -> Unit, onHome: () -> Unit) {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("GUT GEMACHT!", color = Accent, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
        Text("${session.known} von ${session.queue.size}", fontSize = 48.sp, fontWeight = FontWeight.ExtraBold)
        Text("Vokabeln gewusst", color = Muted)
        Spacer(Modifier.height(40.dp))
        Button(onClick = onAgain, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp)) {
            Text("Nochmal lernen", fontWeight = FontWeight.Bold)
        }
        TextButton(onClick = onHome) { Text("Zurück zum Home", color = Muted) }
    }
}
