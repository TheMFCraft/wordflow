package app.wordflow.trainer.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.wordflow.trainer.UiState
import app.wordflow.trainer.VocabPair
import app.wordflow.trainer.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    state: UiState,
    onBack: () -> Unit,
    onImport: (String, String, List<VocabPair>) -> Unit,
    recognize: suspend (Bitmap, Boolean) -> String
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var detectedText by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    
    val currentLang = state.user.selectedLang
    val chapters = state.chapters.filter { it.lang == currentLang }
    var selectedChapterId by remember { mutableStateOf(chapters.firstOrNull()?.id ?: "") }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val stream = context.contentResolver.openInputStream(it)
            val b = BitmapFactory.decodeStream(stream)
            bitmap = b
            scope.launch {
                busy = true
                try {
                    detectedText = recognize(b, currentLang == "ja" || currentLang == "zh")
                } catch (e: Exception) {
                    detectedText = "Fehler beim Scannen: ${e.message}"
                }
                busy = false
            }
        }
    }

    val parsedPairs = remember(detectedText) {
        detectedText.lines()
            .filter { it.contains("=") || it.contains("-") || it.contains(":") }
            .map { line ->
                val parts = if (line.contains("=")) line.split("=") 
                            else if (line.contains("-")) line.split("-")
                            else line.split(":")
                VocabPair(parts[0].trim(), parts.getOrNull(1)?.trim() ?: "")
            }
            .filter { it.word.isNotBlank() && it.translation.isNotBlank() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vokabeln scannen") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Outlined.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Bg)
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().background(Bg).padding(padding).padding(20.dp).verticalScroll(rememberScrollState())) {
            if (bitmap == null) {
                Box(
                    Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(24.dp))
                        .background(AccentSoft).clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Outlined.CameraAlt, null, tint = Accent, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(12.dp))
                        Text("Foto auswählen", fontWeight = FontWeight.Bold, color = Accent)
                    }
                }
            } else {
                Image(
                    bitmap!!.asImageBitmap(), null,
                    modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(24.dp))
                )
                Spacer(Modifier.height(12.dp))
                TextButton(onClick = { launcher.launch("image/*") }) {
                    Text("Anderes Foto wählen", color = Accent)
                }
            }

            Spacer(Modifier.height(24.dp))
            Text("ZIEL-KAPITEL", style = MaterialTheme.typography.labelSmall)
            if (chapters.isEmpty()) {
                Text("Bitte erstelle erst ein Kapitel in den Einstellungen!", color = Error)
            } else {
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    OutlinedTextField(
                        value = chapters.find { it.id == selectedChapterId }?.name ?: "Wählen...",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        chapters.forEach { ch ->
                            DropdownMenuItem(
                                text = { Text(ch.name) },
                                onClick = { selectedChapterId = ch.id; expanded = false }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Text("ERGEBNISSE", style = MaterialTheme.typography.labelSmall)
            if (busy) {
                CircularProgressIndicator(color = Accent, modifier = Modifier.align(Alignment.CenterHorizontally).padding(20.dp))
            } else if (parsedPairs.isEmpty() && detectedText.isNotBlank()) {
                Text("Keine Vokabeln erkannt. Format: 'Wort = Übersetzung'", color = Muted, modifier = Modifier.padding(vertical = 12.dp))
            } else {
                parsedPairs.forEach { pair ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Label, null, tint = Accent, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(12.dp))
                        Text(pair.word, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Text(pair.translation, color = Muted)
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
            Button(
                onClick = { onImport(currentLang, selectedChapterId, parsedPairs); onBack() },
                enabled = parsedPairs.isNotEmpty() && selectedChapterId.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Importieren (${parsedPairs.size})", fontWeight = FontWeight.Bold)
            }
        }
    }
}
