package app.wordflow.trainer.ui

import android.Manifest
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.wordflow.trainer.LANGUAGES
import app.wordflow.trainer.ScanParser
import app.wordflow.trainer.UiState
import app.wordflow.trainer.VocabPair
import app.wordflow.trainer.ui.theme.Accent
import app.wordflow.trainer.ui.theme.AccentSoft
import app.wordflow.trainer.ui.theme.Border
import app.wordflow.trainer.ui.theme.Fg
import app.wordflow.trainer.ui.theme.Muted
import app.wordflow.trainer.ui.theme.Success
import app.wordflow.trainer.ui.theme.Surface
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ScanScreen(
    state: UiState,
    onImport: (String, List<VocabPair>) -> Unit,
    recognize: suspend (Bitmap, Boolean) -> String,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var raw by remember { mutableStateOf("") }
    var separator by remember { mutableStateOf(" - ") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var saved by remember { mutableStateOf(false) }
    var lang by remember { mutableStateOf(state.user.selectedLang) }

    fun analyze(bmp: Bitmap) {
        loading = true
        error = null
        saved = false
        scope.launch {
            try {
                val text = recognize(bmp, lang == "ja")
                raw = text
                val best = ScanParser.bestSeparator(text)
                if (ScanParser.parse(text, best).isNotEmpty()) separator = best
                if (text.isBlank()) error = "Kein Text erkannt. Achte auf gutes Licht und eine klare Liste."
            } catch (e: Exception) {
                error = e.message ?: "Analyse fehlgeschlagen"
            } finally {
                loading = false
            }
        }
    }

    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bmp ->
        if (bmp != null) {
            bitmap = bmp
            analyze(bmp)
        }
    }
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        if (ok) camera.launch(null)
        else error = "Kamera-Berechtigung wird für Fotos gebraucht."
    }
    val gallery = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        val bmp = if (Build.VERSION.SDK_INT >= 28) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }.copy(Bitmap.Config.ARGB_8888, false)
        bitmap = bmp
        analyze(bmp)
    }

    val pairs = ScanParser.parse(raw, separator)
    val canSave = pairs.isNotEmpty() && (LANGUAGES.first { it.id == lang }.free || state.user.isPro)

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        Text("SCAN", color = Muted, fontSize = 11.sp, letterSpacing = 1.sp)
        Text("Vokabelliste fotografieren", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(8.dp))
        Text("Mach ein Foto von einer Liste wie „house - Haus“. Danach den Trenner setzen — WordFlow schreibt die Vokabeln raus.", color = Muted)
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { permission.launch(Manifest.permission.CAMERA) }, modifier = Modifier.weight(1f).height(52.dp), colors = ButtonDefaults.buttonColors(Accent), shape = RoundedCornerShape(16.dp)) {
                Text("Foto", fontWeight = FontWeight.Bold)
            }
            Button(onClick = { gallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }, modifier = Modifier.weight(1f).height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = Fg), shape = RoundedCornerShape(16.dp)) {
                Text("Galerie", fontWeight = FontWeight.Bold)
            }
        }
        bitmap?.let {
            Spacer(Modifier.height(16.dp))
            Image(it.asImageBitmap(), contentDescription = "Scan", modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(18.dp)))
        }
        if (loading) {
            Spacer(Modifier.height(16.dp))
            CircularProgressIndicator(color = Accent)
            Text("Text wird gelesen …", color = Muted, modifier = Modifier.padding(top = 8.dp))
        }
        error?.let {
            Spacer(Modifier.height(12.dp))
            Text(it, color = app.wordflow.trainer.ui.theme.Error)
        }
        if (raw.isNotBlank()) {
            Spacer(Modifier.height(16.dp))
            Text("Erkannter Text", fontWeight = FontWeight.SemiBold)
            Text(raw, color = Muted, fontSize = 13.sp, modifier = Modifier.padding(top = 6.dp).fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Surface).border(1.dp, Border, RoundedCornerShape(14.dp)).padding(12.dp))
            Spacer(Modifier.height(16.dp))
            Text("Trenner", fontWeight = FontWeight.SemiBold)
            Text("So sind Wort und Übersetzung getrennt.", color = Muted, fontSize = 13.sp)
            Spacer(Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ScanParser.presets.filter { it != "\t" }.forEach { preset ->
                    val label = if (preset.isBlank()) "Leer" else "„$preset“"
                    val selected = separator == preset
                    Text(
                        label,
                        color = if (selected) Accent else Fg,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clip(RoundedCornerShape(999.dp))
                            .background(if (selected) AccentSoft else Surface)
                            .border(1.dp, if (selected) Accent else Border, RoundedCornerShape(999.dp))
                            .clickable { separator = preset; saved = false }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                    )
                }
                Text("Tab", color = if (separator == "\t") Accent else Fg, fontWeight = FontWeight.SemiBold, modifier = Modifier.clip(RoundedCornerShape(999.dp)).background(if (separator == "\t") AccentSoft else Surface).border(1.dp, if (separator == "\t") Accent else Border, RoundedCornerShape(999.dp)).clickable { separator = "\t"; saved = false }.padding(horizontal = 12.dp, vertical = 8.dp))
            }
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = separator,
                onValueChange = { separator = it; saved = false },
                label = { Text("Eigener Trenner") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Accent, unfocusedBorderColor = Border),
            )
            Spacer(Modifier.height(16.dp))
            Text("Sprache für den Import", fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LANGUAGES.forEach { item ->
                    val selected = lang == item.id
                    Text("${item.flag} ${item.name}", modifier = Modifier.clip(RoundedCornerShape(999.dp)).background(if (selected) AccentSoft else Surface).border(1.dp, if (selected) Accent else Border, RoundedCornerShape(999.dp)).clickable { lang = item.id }.padding(horizontal = 12.dp, vertical = 8.dp), fontWeight = FontWeight.Medium)
                }
            }
            Spacer(Modifier.height(16.dp))
            Text("${pairs.size} Vokabeln erkannt", fontWeight = FontWeight.SemiBold, color = if (pairs.isEmpty()) Muted else Success)
            pairs.take(40).forEach { pair ->
                Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(pair.word, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                    Text(pair.translation, color = Muted, modifier = Modifier.weight(1f))
                }
            }
            if (pairs.size > 40) Text("… und ${pairs.size - 40} weitere", color = Muted)
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    onImport(lang, pairs)
                    saved = true
                },
                enabled = canSave,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(Accent),
                shape = RoundedCornerShape(16.dp),
            ) {
                Text(if (saved) "Gespeichert" else "Vokabeln übernehmen", fontWeight = FontWeight.Bold)
            }
            if (!LANGUAGES.first { it.id == lang }.free && !state.user.isPro) {
                Text("Diese Sprache braucht WordFlow Pro.", color = Muted, modifier = Modifier.padding(top = 8.dp))
            }
        }
        Spacer(Modifier.height(32.dp))
    }
}
