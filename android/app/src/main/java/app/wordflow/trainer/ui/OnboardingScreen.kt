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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.wordflow.trainer.LANGUAGES
import app.wordflow.trainer.ui.theme.Accent
import app.wordflow.trainer.ui.theme.AccentSoft
import app.wordflow.trainer.ui.theme.Bg
import app.wordflow.trainer.ui.theme.Border
import app.wordflow.trainer.ui.theme.Fg
import app.wordflow.trainer.ui.theme.Muted
import app.wordflow.trainer.ui.theme.Surface
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(onDone: (String, String, Int) -> Unit) {
    val pager = rememberPagerState(pageCount = { 4 })
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    var lang by remember { mutableStateOf("es") }
    var goal by remember { mutableIntStateOf(24) }

    fun next() {
        if (pager.currentPage < 3) scope.launch { pager.animateScrollToPage(pager.currentPage + 1) }
        else onDone(name.ifBlank { "Anna" }, lang, goal)
    }

    Column(
        Modifier.fillMaxSize().background(Bg).padding(horizontal = 24.dp, vertical = 32.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(4) { i ->
                Box(
                    Modifier.weight(1f).height(4.dp).clip(CircleShape)
                        .background(if (i <= pager.currentPage) Accent else Border),
                )
            }
        }
        HorizontalPager(state = pager, modifier = Modifier.weight(1f), userScrollEnabled = false) { page ->
            when (page) {
                0 -> Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("WordFlow", fontSize = 42.sp, fontWeight = FontWeight.ExtraBold, color = Accent)
                    Spacer(Modifier.height(12.dp))
                    Text("Vokabeln, die bleiben.", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Fg, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(8.dp))
                    Text("Lerne mit echten Wortschätzen, Karteikarten und Foto-Scan — warm, klar, alltagstauglich.", color = Muted, textAlign = TextAlign.Center)
                }
                1 -> Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
                    Text("WIE SOLLEN WIR DICH NENNEN?", style = androidx.compose.material3.MaterialTheme.typography.labelSmall)
                    Spacer(Modifier.height(8.dp))
                    Text("Dein Name", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { if (it.length <= 24) name = it },
                        placeholder = { Text("z. B. Anna") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Accent, unfocusedBorderColor = Border, focusedContainerColor = Surface, unfocusedContainerColor = Surface),
                    )
                }
                2 -> Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.Center) {
                    Text("SPRACHE", style = androidx.compose.material3.MaterialTheme.typography.labelSmall)
                    Spacer(Modifier.height(8.dp))
                    Text("Was willst du lernen?", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.height(16.dp))
                    LANGUAGES.forEach { item ->
                        val selected = lang == item.id
                        Row(
                            Modifier.fillMaxWidth().padding(bottom = 10.dp).clip(RoundedCornerShape(18.dp))
                                .border(if (selected) 2.dp else 1.dp, if (selected) Accent else Border, RoundedCornerShape(18.dp))
                                .background(if (selected) AccentSoft else Surface)
                                .clickable { lang = item.id }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(item.flag, fontSize = 28.sp, modifier = Modifier.padding(end = 12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(item.name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                                Text("${item.pack} · ${item.level}${if (!item.free) " · Pro" else ""}", color = Muted, fontSize = 13.sp)
                            }
                        }
                    }
                }
                else -> Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
                    Text("TAGESZIEL", style = androidx.compose.material3.MaterialTheme.typography.labelSmall)
                    Spacer(Modifier.height(8.dp))
                    Text("Wie viele Wörter am Tag?", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.height(16.dp))
                    listOf(10, 20, 24, 40).forEach { value ->
                        val selected = goal == value
                        Box(
                            Modifier.fillMaxWidth().padding(bottom = 10.dp).clip(RoundedCornerShape(18.dp))
                                .border(if (selected) 2.dp else 1.dp, if (selected) Accent else Border, RoundedCornerShape(18.dp))
                                .background(if (selected) AccentSoft else Surface)
                                .clickable { goal = value }
                                .padding(18.dp),
                        ) {
                            Text("$value Wörter", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
        Button(
            onClick = ::next,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Accent),
        ) {
            Text(if (pager.currentPage == 3) "Loslegen" else "Weiter", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Spacer(Modifier.height(12.dp))
    }
}
