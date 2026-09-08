package app.wordflow.trainer.ui

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.wordflow.trainer.UiState
import app.wordflow.trainer.ui.theme.Accent
import app.wordflow.trainer.ui.theme.AccentSoft
import app.wordflow.trainer.ui.theme.Bg
import app.wordflow.trainer.ui.theme.Border
import app.wordflow.trainer.ui.theme.Fg
import app.wordflow.trainer.ui.theme.Muted
import app.wordflow.trainer.ui.theme.Success
import app.wordflow.trainer.ui.theme.Surface

@Composable
fun ProScreen(state: UiState, onActivate: () -> Unit, onCancel: () -> Unit, onClose: () -> Unit) {
    var yearly by remember { mutableStateOf(true) }
    Column(Modifier.fillMaxSize().background(Bg).verticalScroll(rememberScrollState()).padding(20.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("UPGRADE", color = Muted, fontSize = 11.sp, letterSpacing = 1.sp)
                Text("WordFlow Pro", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
            }
            IconButton(onClick = onClose) { Icon(Icons.Outlined.Close, contentDescription = "Schließen") }
        }
        Spacer(Modifier.height(16.dp))
        Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Accent).padding(24.dp)) {
            Text(if (state.user.isPro) "AKTIV" else "LERN OHNE GRENZEN", color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.72f), fontSize = 12.sp)
            Text(if (state.user.isPro) "Du lernst mit Pro." else "Freunde dich mit jeder Sprache an.", color = androidx.compose.ui.graphics.Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
            Text(if (state.user.isPro) "Alle Sprachen und Foto-Scan-Importe sind frei." else "Unbegrenzt Sprachen, Listen und Scan.", color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.85f), modifier = Modifier.padding(top = 6.dp))
        }
        Spacer(Modifier.height(16.dp))
        Feature("Unlimited Sprachen", "Japanisch, Italienisch und Englisch dazu.")
        Feature("Foto-Scan", "Listen fotografieren, Trenner setzen, Vokabeln speichern.")
        Feature("Echte Wortschätze", "Alltagswörter mit Beispielen, nicht nur Platzhalter.")
        Spacer(Modifier.height(16.dp))
        if (state.user.isPro) {
            Button(onClick = onCancel, modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(Accent), shape = RoundedCornerShape(16.dp)) {
                Text("Pro beenden (Demo)")
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                Plan("Monatlich", "4,99€", "pro Monat", !yearly, Modifier.weight(1f)) { yearly = false }
                Plan("Jährlich", "29,99€", "pro Jahr", yearly, Modifier.weight(1f), badge = "BELIEBT") { yearly = true }
            }
            Spacer(Modifier.height(16.dp))
            Button(onClick = onActivate, modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(Accent), shape = RoundedCornerShape(16.dp)) {
                Text("7 Tage kostenlos testen", fontWeight = FontWeight.Bold)
            }
            Text("Danach ${if (yearly) "29,99€/Jahr" else "4,99€/Monat"}. Demo ohne Zahlung.", color = Muted, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun Feature(title: String, desc: String) {
    Column(Modifier.fillMaxWidth().padding(vertical = 8.dp).clip(RoundedCornerShape(16.dp)).background(Surface).border(1.dp, Border, RoundedCornerShape(16.dp)).padding(16.dp)) {
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
