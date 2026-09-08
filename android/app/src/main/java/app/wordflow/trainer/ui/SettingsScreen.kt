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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.wordflow.trainer.CyloneIdAuth
import app.wordflow.trainer.UiState
import app.wordflow.trainer.VocabRepository
import app.wordflow.trainer.ui.theme.Accent
import app.wordflow.trainer.ui.theme.Accent2
import app.wordflow.trainer.ui.theme.AccentSoft
import app.wordflow.trainer.ui.theme.Border
import app.wordflow.trainer.ui.theme.Fg
import app.wordflow.trainer.ui.theme.Muted
import app.wordflow.trainer.ui.theme.Success
import app.wordflow.trainer.ui.theme.Surface

@Composable
fun SettingsScreen(
    state: UiState,
    onName: (String) -> Unit,
    onGoal: (Int) -> Unit,
    onPlus: (Boolean) -> Unit,
    onCyloneLogin: () -> Unit,
    onCyloneLogout: () -> Unit,
) {
    val user = state.user
    val context = LocalContext.current
    var name by remember(user.name) { mutableStateOf(user.name) }
    var yearly by remember { mutableStateOf(true) }
    val acc = if (user.totalAttempts == 0) 0 else user.totalCorrect * 100 / user.totalAttempts
    val hours = user.totalSeconds / 3600.0
    val timeLabel = if (hours < 1) "${user.totalSeconds / 60}m" else "${"%.1f".format(hours)}h"

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        Text("KONTO & APP", color = Muted, fontSize = 11.sp, letterSpacing = 1.sp)
        Text("Einstellungen", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(16.dp))

        Text("Profil", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { if (it.length <= 24) { name = it; onName(it) } },
            label = { Text("Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Accent, unfocusedBorderColor = Border, focusedContainerColor = Surface, unfocusedContainerColor = Surface),
        )
        Spacer(Modifier.height(16.dp))

        Text("Cylone ID", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        Spacer(Modifier.height(8.dp))
        Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Surface).border(1.dp, Border, RoundedCornerShape(18.dp)).padding(16.dp)) {
            if (user.isCyloneLinked) {
                Text(user.cyloneName.ifBlank { user.name }, fontWeight = FontWeight.SemiBold)
                Text(user.cyloneEmail.ifBlank { "Verbunden" }, color = Muted, fontSize = 13.sp)
                Spacer(Modifier.height(12.dp))
                Button(onClick = onCyloneLogout, colors = ButtonDefaults.buttonColors(containerColor = Fg), shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) {
                    Text("Abmelden")
                }
            } else {
                Text("Melde dich mit Cylone ID an, um WordFlow PLUS und später Cloud-Sync zu nutzen.", color = Muted, fontSize = 13.sp)
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = {
                        onCyloneLogin()
                        (context as? Activity)?.let { CyloneIdAuth.startLogin(it) }
                    },
                    colors = ButtonDefaults.buttonColors(Accent),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("Mit Cylone ID anmelden", fontWeight = FontWeight.Bold) }
                if (!state.cyloneConfigured) {
                    Text("Client-ID noch nicht hinterlegt (cylone.clientId in local.properties).", color = Muted, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                }
            }
            state.authMessage?.let {
                Text(it, color = Muted, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
            }
        }

        Spacer(Modifier.height(20.dp))
        Text("Tagesziel", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(10, 20, 24, 40).forEach { value ->
                val on = user.dailyGoal == value
                Box(
                    Modifier.weight(1f).clip(RoundedCornerShape(14.dp))
                        .background(if (on) AccentSoft else Surface)
                        .border(1.dp, if (on) Accent else Border, RoundedCornerShape(14.dp))
                        .clickable { onGoal(value) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("$value", fontWeight = FontWeight.Bold, color = if (on) Accent else Fg)
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        Text("Statistik", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatMini("${state.learnedTotal}", "Gelernt", Fg, Modifier.weight(1f))
            StatMini(timeLabel, "Zeit", Accent2, Modifier.weight(1f))
            StatMini("$acc%", "Genau", Success, Modifier.weight(1f))
        }
        Spacer(Modifier.height(10.dp))
        Column(Modifier.clip(RoundedCornerShape(18.dp)).background(Surface).border(1.dp, Border, RoundedCornerShape(18.dp)).padding(16.dp).fillMaxWidth()) {
            if (state.activity.isEmpty()) {
                Text("Noch keine Aktivität. Starte eine Lektion oder lege Vokabeln an.", color = Muted)
            } else {
                state.activity.take(6).forEach { item ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(Modifier.weight(1f)) {
                            Text(item.title, fontWeight = FontWeight.Medium)
                            Text("${item.words} Wörter · ${item.accuracy}% richtig", color = Muted, fontSize = 12.sp)
                        }
                        Text(VocabRepository.formatWhen(item.date), color = Muted, fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(Modifier.height(28.dp))
        Text("ABO", color = Muted, fontSize = 11.sp, letterSpacing = 1.sp)
        Text("WordFlow PLUS", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(12.dp))
        Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Accent).padding(24.dp)) {
            Text(if (user.isPlus) "AKTIV" else "MEHR AUS WORDFLOW", color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.72f), fontSize = 12.sp)
            Text(if (user.isPlus) "Du nutzt WordFlow PLUS." else "Cloud Sync, AI Trainer und mehr.", color = androidx.compose.ui.graphics.Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        }
        Spacer(Modifier.height(12.dp))
        Feature("Cloud Sync", "Vokabeln und Fortschritt später über Geräte hinweg.")
        Feature("AI Trainer", "Personalisierte Übungen und Erklärungen.")
        Feature("Foto-Scan+", "Listen fotografieren und direkt in Kapitel importieren.")
        Feature("Priorisierter Support", "Schneller Feedback-Kanal für PLUS-Mitglieder.")
        Spacer(Modifier.height(12.dp))
        if (user.isPlus) {
            Button(onClick = { onPlus(false) }, modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(Accent), shape = RoundedCornerShape(16.dp)) {
                Text("PLUS beenden (Demo)")
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                Plan("Monatlich", "4,99€", "pro Monat", !yearly, Modifier.weight(1f)) { yearly = false }
                Plan("Jährlich", "29,99€", "pro Jahr", yearly, Modifier.weight(1f), badge = "BELIEBT") { yearly = true }
            }
            Spacer(Modifier.height(16.dp))
            Button(onClick = { onPlus(true) }, modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(Accent), shape = RoundedCornerShape(16.dp)) {
                Text("7 Tage kostenlos testen", fontWeight = FontWeight.Bold)
            }
            Text("Danach ${if (yearly) "29,99€/Jahr" else "4,99€/Monat"}. Demo ohne Zahlung.", color = Muted, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
        }
        Spacer(Modifier.height(24.dp))
        Text("WordFlow 2.1.0", color = Muted, fontSize = 12.sp)
        Spacer(Modifier.height(48.dp))
    }
}

@Composable
private fun Feature(title: String, desc: String) {
    Column(Modifier.fillMaxWidth().padding(vertical = 6.dp).clip(RoundedCornerShape(16.dp)).background(Surface).border(1.dp, Border, RoundedCornerShape(16.dp)).padding(16.dp)) {
        Text(title, fontWeight = FontWeight.SemiBold)
        Text(desc, color = Muted, fontSize = 13.sp)
    }
}

@Composable
private fun Plan(name: String, price: String, period: String, selected: Boolean, modifier: Modifier, badge: String? = null, onClick: () -> Unit) {
    Box(modifier.clip(RoundedCornerShape(20.dp)).border(2.dp, if (selected) Accent else Border, RoundedCornerShape(20.dp)).background(Surface).clickable(onClick = onClick).padding(16.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            badge?.let {
                Text(it, color = androidx.compose.ui.graphics.Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clip(RoundedCornerShape(999.dp)).background(Accent).padding(horizontal = 8.dp, vertical = 2.dp))
            }
            Text(name, fontWeight = FontWeight.Bold)
            Text(price, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            Text(period, color = Muted, fontSize = 12.sp)
            if (name == "Jährlich") Text("Spare 50%", color = Success, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun StatMini(value: String, label: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier) {
    Column(modifier.clip(RoundedCornerShape(16.dp)).background(Surface).border(1.dp, Border, RoundedCornerShape(16.dp)).padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
        Text(label, color = Muted, fontSize = 11.sp)
    }
}
