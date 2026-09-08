package app.wordflow.trainer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.wordflow.trainer.LanguagePack
import app.wordflow.trainer.UiState
import app.wordflow.trainer.ui.theme.*

@Composable
fun StoreScreen(state: UiState) {
    val packs = listOf(
        LanguagePack("pn-la", "la", "Prima Nova Latein", "Vollständiger Wortschatz zum Lehrbuch Prima Nova. Alle Lektionen 1-45.", "4,99€"),
        LanguagePack("la-basis", "la", "Latein Grundwortschatz", "Die 500 häufigsten Wörter für das Latinum.", "Kostenlos"),
        LanguagePack("es-reisen", "es", "Spanisch für den Urlaub", "Wichtige Sätze und Wörter für deine Reise nach Spanien.", "Kostenlos")
    )

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        Text("WORDFLOW STORE", style = MaterialTheme.typography.labelSmall)
        Text("Language Packs", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Nach Büchern oder Sprachen suchen...") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            leadingIcon = { Icon(Icons.Outlined.Search, null) },
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Accent, unfocusedBorderColor = Border)
        )

        Spacer(Modifier.height(24.dp))
        Text("EMPFOHLEN", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(Modifier.height(12.dp))

        packs.forEach { pack ->
            PackItem(pack)
        }
    }
}

@Composable
private fun PackItem(pack: LanguagePack) {
    Column(
        Modifier.fillMaxWidth().padding(bottom = 16.dp).clip(RoundedCornerShape(20.dp))
            .background(Surface).border(1.dp, Border, RoundedCornerShape(20.dp)).padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(AccentSoft), contentAlignment = Alignment.Center) {
                Text(if (pack.langId == "la") "📜" else "🌍", fontSize = 24.sp)
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(pack.name, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Text(pack.price, color = Accent, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
            IconButton(onClick = { /* Download logic */ }) {
                Icon(Icons.Outlined.FileDownload, null, tint = Muted)
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(pack.description, color = Muted, fontSize = 13.sp)
    }
}
