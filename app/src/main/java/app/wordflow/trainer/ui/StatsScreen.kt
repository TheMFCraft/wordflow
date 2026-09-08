package app.wordflow.trainer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.wordflow.trainer.UiState
import app.wordflow.trainer.ui.theme.*

@Composable
fun StatsScreen(state: UiState) {
    val user = state.user
    val totalHours = user.totalSeconds / 3600
    val totalMinutes = (user.totalSeconds % 3600) / 60

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text("DEINE FORTSCHRITTE", style = MaterialTheme.typography.labelSmall)
        Text("Statistiken", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(24.dp))

        // Key Metrics Row
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                label = "Gelernt",
                value = "${state.learnedTotal}",
                icon = Icons.Outlined.CheckCircle,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "Zeit",
                value = if (totalHours > 0) "${totalHours}h ${totalMinutes}m" else "${totalMinutes}m",
                icon = Icons.Outlined.Timer,
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(Modifier.height(24.dp))
        Text("AKTIVITÄT", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(Modifier.height(12.dp))
        
        if (state.activity.isEmpty()) {
            Box(Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                Text("Noch keine Lektionen abgeschlossen.", color = Muted)
            }
        } else {
            state.activity.forEach { item ->
                ActivityItem(item.title, item.words, item.accuracy, item.date)
            }
        }
        
        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun StatCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Icon(icon, null, tint = Accent, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(8.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            Text(label, color = Muted, fontSize = 12.sp)
        }
    }
}

@Composable
private fun ActivityItem(title: String, count: Int, accuracy: Int, date: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Surface)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold)
            Text("$date · $count Wörter", color = Muted, fontSize = 12.sp)
        }
        Text("$accuracy%", color = if (accuracy >= 80) Success else if (accuracy >= 50) Accent else Error, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
    }
}
