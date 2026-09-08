package app.wordflow.trainer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.wordflow.trainer.LANGUAGES
import app.wordflow.trainer.UiState
import app.wordflow.trainer.ui.theme.*

private enum class AddStep { Menu, Language, Chapter, Vocab }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContentSheet(
    state: UiState,
    onDismiss: () -> Unit,
    onAddLanguage: (String) -> Unit,
    onAddChapter: (String, String) -> Unit,
    onAddWord: (String, String, String, String) -> Unit,
    onScan: () -> Unit,
) {
    val sheet = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var step by remember { mutableStateOf(AddStep.Menu) }
    val selectedLangs = state.user.selectedLangIds.toSet()
    val available = LANGUAGES.filter { it.id !in selectedLangs }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheet, containerColor = Surface) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp).padding(bottom = 32.dp).verticalScroll(rememberScrollState())) {
            when (step) {
                AddStep.Menu -> {
                    Text("HINZUFÜGEN", style = MaterialTheme.typography.labelSmall)
                    Spacer(Modifier.height(16.dp))
                    AddRow("Sprache", "Lernliste erweitern", Icons.Outlined.Language) { step = AddStep.Language }
                    AddRow("Kapitel", "Neues Kapitel anlegen", Icons.Outlined.Book) { step = AddStep.Chapter }
                    AddRow("Vokabel", "Manuell hinzufügen", Icons.Outlined.Add) { step = AddStep.Vocab }
                }
                AddStep.Language -> {
                    Text("SPRACHE WÄHLEN", style = MaterialTheme.typography.labelSmall)
                    Spacer(Modifier.height(12.dp))
                    available.forEach { lang ->
                        Row(
                            Modifier.fillMaxWidth().padding(bottom = 8.dp).clip(RoundedCornerShape(12.dp))
                                .border(1.dp, Border, RoundedCornerShape(12.dp))
                                .clickable { onAddLanguage(lang.id); onDismiss() }.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(lang.flag, fontSize = 24.sp)
                            Spacer(Modifier.width(16.dp))
                            Text(lang.name, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                AddStep.Chapter -> ChapterForm(state) { l, n -> onAddChapter(l, n); onDismiss() }
                AddStep.Vocab -> VocabForm(state) { l, c, w, t -> onAddWord(l, c, w, t); onDismiss() }
            }
        }
    }
}

@Composable
private fun AddRow(title: String, desc: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(bottom = 12.dp).clip(RoundedCornerShape(16.dp))
            .background(AccentSoft).clickable(onClick = onClick).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = Accent)
        Spacer(Modifier.width(16.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold)
            Text(desc, color = Muted, fontSize = 12.sp)
        }
    }
}

@Composable
private fun ChapterForm(state: UiState, onAdd: (String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var lang by remember { mutableStateOf(state.user.selectedLang) }
    Column {
        Text("KAPITEL ERSTELLEN", style = MaterialTheme.typography.labelSmall)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name (z.B. Lektion 1)") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        Button(onClick = { onAdd(lang, name) }, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp)) {
            Text("Anlegen", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun VocabForm(state: UiState, onAdd: (String, String, String, String) -> Unit) {
    var word by remember { mutableStateOf("") }
    var trans by remember { mutableStateOf("") }
    val lang = state.user.selectedLang
    val chapters = state.chapters.filter { it.lang == lang }
    var chapterId by remember { mutableStateOf(chapters.firstOrNull()?.id ?: "") }

    Column {
        Text("VOKABEL HINZUFÜGEN", style = MaterialTheme.typography.labelSmall)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(value = word, onValueChange = { word = it }, label = { Text("Wort") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = trans, onValueChange = { trans = it }, label = { Text("Übersetzung") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        if (chapters.isEmpty()) {
            Text("Erstelle erst ein Kapitel!", color = Error)
        } else {
            Button(onClick = { onAdd(lang, chapterId, word, trans) }, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp)) {
                Text("Speichern", fontWeight = FontWeight.Bold)
            }
        }
    }
}
