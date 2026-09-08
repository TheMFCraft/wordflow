package app.wordflow.trainer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.wordflow.trainer.ui.theme.Accent
import app.wordflow.trainer.ui.theme.Muted

class CradleBarShape : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val corner = with(density) { 28.dp.toPx() }
        val radius = with(density) { 38.dp.toPx() }
        val cx = size.width / 2f
        val path = Path().apply {
            moveTo(0f, corner)
            quadraticTo(0f, 0f, corner, 0f)
            lineTo(cx - radius, 0f)
            arcTo(
                Rect(cx - radius, -radius, cx + radius, radius),
                180f,
                180f,
                false,
            )
            lineTo(size.width - corner, 0f)
            quadraticTo(size.width, 0f, size.width, corner)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        return Outline.Generic(path)
    }
}

@Composable
fun WordFlowBottomBar(
    selected: String,
    onHome: () -> Unit,
    onPlus: () -> Unit,
    onSettings: () -> Unit,
) {
    Box(
        Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .height(96.dp)
            .padding(horizontal = 12.dp),
    ) {
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(64.dp)
                .shadow(16.dp, CradleBarShape())
                .clip(CradleBarShape())
                .background(Color.White),
        ) {
            Row(Modifier.matchParentSize().padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                BarItem(
                    selected = selected == "home",
                    label = "Home",
                    onClick = onHome,
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(Icons.Outlined.Home, contentDescription = "Home", tint = if (selected == "home") Accent else Muted)
                }
                Spacer(Modifier.width(80.dp))
                BarItem(
                    selected = selected == "settings",
                    label = "Einstellungen",
                    onClick = onSettings,
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(Icons.Outlined.Settings, contentDescription = "Einstellungen", tint = if (selected == "settings") Accent else Muted)
                }
            }
        }
        Box(
            Modifier
                .align(Alignment.TopCenter)
                .offset(y = 4.dp)
                .size(64.dp)
                .shadow(12.dp, CircleShape)
                .clip(CircleShape)
                .background(Accent)
                .clickable(onClick = onPlus),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.Add, contentDescription = "Hinzufügen", tint = Color.White, modifier = Modifier.size(32.dp))
        }
    }
}

@Composable
private fun BarItem(
    selected: Boolean,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier,
    icon: @Composable () -> Unit,
) {
    Box(modifier.clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        androidx.compose.foundation.layout.Column(horizontalAlignment = Alignment.CenterHorizontally) {
            icon()
            Text(label, color = if (selected) Accent else Muted, fontSize = 11.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium)
        }
    }
}
