package app.wordflow.trainer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material.icons.outlined.Spellcheck
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.wordflow.trainer.LANGUAGES
import app.wordflow.trainer.UiState
import app.wordflow.trainer.ui.theme.Accent
import app.wordflow.trainer.ui.theme.AccentSoft
import app.wordflow.trainer.ui.theme.Border
import app.wordflow.trainer.ui.theme.Fg
import app.wordflow.trainer.ui.theme.Muted
import app.wordflow.trainer.ui.theme.Surface

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

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheet,
        containerColor = Surface,
    ) {
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 28.dp).verticalScroll(rememberScrollState()),
        ) {
            when (step) {
                AddStep.Menu -> {
                    Text("HINZUFÜGEN", color = Muted, fontSize = 11.sp, letterSpacing = 1.sp)
                    Text("Was möchtest du anlegen?", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 4.dp, bottom = 16.dp))
                    AddRow("Sprache", "Eine neue Lernsprache zu deiner Liste", Icons.Outlined.Translate) { step = AddStep.Language }
                    AddRow("Kapitel", "Ein benanntes Kapitel in einer Sprache", Icons.Outlined.AutoStories) { step = AddStep.Chapter }
                    AddRow("Vokabeln", "Wort und Übersetzung manuell eintragen", Icons.Outlined.Spellcheck) { step = AddStep.Vocab }
                    AddRow("Foto-Scan", "Liste fotografieren und übernehmen", Icons.Outlined.DocumentScanner, onScan)
                }
                AddStep.Language -> {
                    Text("SPRACHE", color = Muted, fontSize = 11.sp, letterSpacing = 1.sp)
                    Text("Sprache hinzufügen", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 4.dp, bottom = 12.dp))
                    if (available.isEmpty()) {
                        Text("Du lernst bereits alle verfügbaren Sprachen.", color = Muted)
                    } else {
                        available.forEach { item ->
                            Row(
                                Modifier.fillMaxWidth().padding(bottom = 8.dp).clip(RoundedCornerShape(16.dp))
                                    .border(1.dp, Border, RoundedCornerShape(16.dp))
                                    .clickable { onAddLanguage(item.id); onDismiss() }
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(item.flag, fontSize = 24.sp, modifier = Modifier.padding(end = 12.dp))
                                Column {
                                    Text(item.name, fontWeight = FontWeight.SemiBold)
                                    Text("${item.pack} · ${item.level}", color = Muted, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
                AddStep.Chapter -> ChapterForm(state, onAdd = { lang, name -> onAddChapter(lang, name); onDismiss() })
                AddStep.Vocab -> VocabForm(state, onAdd = { lang, chapter, word, translation ->
                    onAddWord(lang, chapter, word, translation)
                    onDismiss()
                })
            }
        }
    }
}

@Composable
private fun AddRow(title: String, desc: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(bottom = 10.dp).clip(RoundedCornerShape(18.dp))
            .background(AccentSoft)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(icon, contentDescription = null, tint = Accent)
        Column {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Text(desc, color = Muted, fontSize = 13.sp)
        }
    }
}

@Composable
private fun ChapterForm(state: UiState, onAdd: (String, String) -> Unit) {
    var lang by remember { mutableStateOf(state.user.selectedLang.ifBlank { state.user.selectedLangIds.firstOrNull().orEmpty() }) }
    var name by remember { mutableStateOf("") }
    Text("KAPITEL", color = Muted, fontSize = 11.sp, letterSpacing = 1.sp)
    Text("Kapitel benennen", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 4.dp, bottom = 12.dp))
    Text("Sprache", fontWeight = FontWeight.SemiBold)
    Spacer(Modifier.height(8.dp))
    LangChips(state.user.selectedLangIds, lang) { lang = it }
    Spacer(Modifier.height(12.dp))
    OutlinedTextField(
        value = name,
        onValueChange = { if (it.length <= 40) name = it },
        placeholder = { Text("z. B. Reisen, Küche, Verben") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Accent, unfocusedBorderColor = Border),
    )
    Spacer(Modifier.height(16.dp))
    Button(
        onClick = { onAdd(lang, name) },
        enabled = lang.isNotBlank() && name.isNotBlank(),
        modifier = Modifier.fillMaxWidth().height(52.dp),
        colors = ButtonDefaults.buttonColors(Accent),
        shape = RoundedCornerShape(16.dp),
    ) { Text("Kapitel anlegen", fontWeight = FontWeight.Bold) }
}

@Composable
private fun VocabForm(state: UiState, onAdd: (String, String, String, String) -> Unit) {
    var lang by remember { mutableStateOf(state.user.selectedLang.ifBlank { state.user.selectedLangIds.firstOrNull().orEmpty() }) }
    val chapters = state.chapters.filter { it.lang == lang }
    var chapterId by remember { mutableStateOf(chapters.firstOrNull()?.id.orEmpty()) }
    var word by remember { mutableStateOf("") }
    var translation by remember { mutableStateOf("") }
    Text("VOKABEL", color = Muted, fontSize = 11.sp, letterSpacing = 1.sp)
    Text("Neue Vokabel", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 4.dp, bottom = 12.dp))
    Text("Sprache", fontWeight = FontWeight.SemiBold)
    Spacer(Modifier.height(8.dp))
    LangChips(state.user.selectedLangIds, lang) {
        lang = it
        chapterId = state.chapters.firstOrNull { chapter -> chapter.lang == it }?.id.orEmpty()
    }
    Spacer(Modifier.height(12.dp))
    Text("Kapitel", fontWeight = FontWeight.SemiBold)
    Spacer(Modifier.height(8.dp))
    if (chapters.isEmpty()) {
        Text("Lege zuerst ein Kapitel in dieser Sprache an.", color = Muted)
    } else {
        chapters.forEach { chapter ->
            val on = chapter.id == chapterId
            Text(
                chapter.name,
                fontWeight = FontWeight.SemiBold,
                color = if (on) Accent else Fg,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).clip(RoundedCornerShape(14.dp))
                    .background(if (on) AccentSoft else Surface)
                    .border(1.dp, if (on) Accent else Border, RoundedCornerShape(14.dp))
                    .clickable { chapterId = chapter.id }
                    .padding(12.dp),
            )
        }
    }
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(
        value = word,
        onValueChange = { word = it },
        placeholder = { Text("Wort") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Accent, unfocusedBorderColor = Border),
    )
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(
        value = translation,
        onValueChange = { translation = it },
        placeholder = { Text("Übersetzung") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Accent, unfocusedBorderColor = Border),
    )
    Spacer(Modifier.height(16.dp))
    Button(
        onClick = { onAdd(lang, chapterId, word, translation) },
        enabled = lang.isNotBlank() && chapterId.isNotBlank() && word.isNotBlank() && translation.isNotBlank(),
        modifier = Modifier.fillMaxWidth().height(52.dp),
        colors = ButtonDefaults.buttonColors(Accent),
        shape = RoundedCornerShape(16.dp),
    ) { Text("Vokabel speichern", fontWeight = FontWeight.Bold) }
}

@Composable
private fun LangChips(ids: List<String>, selected: String, onSelect: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        LANGUAGES.filter { it.id in ids }.forEach { item ->
            val on = item.id == selected
            Text(
                "${item.flag}  ${item.name}",
                fontWeight = FontWeight.SemiBold,
                color = if (on) Accent else Fg,
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                    .background(if (on) AccentSoft else Surface)
                    .border(1.dp, if (on) Accent else Border, RoundedCornerShape(14.dp))
                    .clickable { onSelect(item.id) }
                    .padding(12.dp),
            )
        }
    }
}
