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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import app.wordflow.trainer.CyloneIdAuth
import app.wordflow.trainer.UiState
import app.wordflow.trainer.ui.theme.*

private enum class SettingsPage { Main, Profile, Account, Subscription }

@Composable
fun SettingsScreen(
    state: UiState,
    onName: (String) -> Unit,
    onGoal: (Int) -> Unit,
    onPlus: (Boolean) -> Unit,
    onCyloneLogin: () -> Unit,
    onCyloneLogout: () -> Unit,
) {
    var currentPage by remember { mutableStateOf(SettingsPage.Main) }

    Box(Modifier.fillMaxSize()) {
        when (currentPage) {
            SettingsPage.Main -> MainSettings(state) { currentPage = it }
            SettingsPage.Profile -> ProfileSettings(state, onName, onGoal) { currentPage = SettingsPage.Main }
            SettingsPage.Account -> AccountSettings(state, onCyloneLogin, onCyloneLogout) { currentPage = SettingsPage.Main }
            SettingsPage.Subscription -> PlusSettings(state, onPlus) { currentPage = SettingsPage.Main }
        }
    }
}

@Composable
private fun MainSettings(state: UiState, onNavigate: (SettingsPage) -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        Text("EINSTELLUNGEN", style = MaterialTheme.typography.labelSmall)
        Text("WordFlow", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(24.dp))
        
        SettingCategory("Profil", "Name und Tagesziel", Icons.Outlined.Person) { onNavigate(SettingsPage.Profile) }
        SettingCategory("Cylone ID", "Konto und Synchronisation", Icons.Outlined.AccountCircle) { onNavigate(SettingsPage.Account) }
        SettingCategory("WordFlow PLUS", "Premium-Features verwalten", Icons.Outlined.WorkspacePremium) { onNavigate(SettingsPage.Subscription) }
        
        Spacer(Modifier.weight(1f))
        Text("WordFlow 3.0.0", color = Muted, fontSize = 12.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}

@Composable
private fun ProfileSettings(state: UiState, onName: (String) -> Unit, onGoal: (Int) -> Unit, onBack: () -> Unit) {
    var name by remember { mutableStateOf(state.user.name) }
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        SettingsHeader("Profil", onBack)
        Text("DEIN NAME", style = MaterialTheme.typography.labelSmall)
        OutlinedTextField(
            value = name,
            onValueChange = { name = it; onName(it) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(Modifier.height(24.dp))
        Text("TAGESZIEL", style = MaterialTheme.typography.labelSmall)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(10, 24, 40).forEach { goal ->
                val isOn = state.user.dailyGoal == goal
                Box(
                    Modifier.weight(1f).clip(RoundedCornerShape(12.dp))
                        .background(if (isOn) Accent else Surface)
                        .border(1.dp, Border, RoundedCornerShape(12.dp))
                        .clickable { onGoal(goal) }
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("$goal", color = if (isOn) Color.White else Fg, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun AccountSettings(state: UiState, onLogin: () -> Unit, onLogout: () -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        SettingsHeader("Cylone ID", onBack)
        if (state.user.isCyloneLinked) {
            Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Surface).border(1.dp, Border, RoundedCornerShape(20.dp)).padding(24.dp)) {
                Column {
                    Text("Verbunden mit:", color = Muted, style = MaterialTheme.typography.labelSmall)
                    Text(state.user.cyloneEmail, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text(state.user.cyloneName, color = Muted, fontSize = 14.sp)
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = onLogout,
                        colors = ButtonDefaults.buttonColors(containerColor = CyloneGrey),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    ) {
                        Text("Abmelden")
                    }
                }
            }
        } else {
            Text("Melde dich an, um deinen Fortschritt in der Cloud zu speichern und WordFlow PLUS auf allen Geräten zu nutzen.", color = Muted)
            Spacer(Modifier.height(24.dp))
            // Dark grey icon-only button as requested
            Button(
                onClick = { 
                    onLogin()
                    (context as? android.app.Activity)?.let { app.wordflow.trainer.CyloneIdAuth.startLogin(it) }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyloneGrey),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                if (state.authBusy) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            }
        }
    }
}

@Composable
private fun PlusSettings(state: UiState, onPlus: (Boolean) -> Unit, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        SettingsHeader("WordFlow PLUS", onBack)
        Card(
            Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Accent),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(Modifier.padding(24.dp)) {
                Text("WordFlow PLUS", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                Text(if (state.user.isPlus) "Aktiv" else "Inaktiv", color = Color.White.copy(0.8f))
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { onPlus(!state.user.isPlus) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Accent),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (state.user.isPlus) "Abo beenden" else "Jetzt upgraden")
                }
            }
        }
    }
}

@Composable
private fun SettingsHeader(title: String, onBack: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 24.dp)) {
        IconButton(onClick = onBack) { Icon(Icons.Outlined.ArrowBack, null) }
        Text(title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SettingCategory(title: String, desc: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(bottom = 12.dp).clip(RoundedCornerShape(16.dp))
            .background(Surface).border(1.dp, Border, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = Accent, modifier = Modifier.size(28.dp))
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold)
            Text(desc, color = Muted, fontSize = 12.sp)
        }
        Icon(Icons.Outlined.ChevronRight, null, tint = Border)
    }
}
