package app.wordflow.trainer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Typography

val Bg = Color(0xFFF8F5F0)
val Surface = Color(0xFFFFFFFF)
val Fg = Color(0xFF2C2926)
val Muted = Color(0xFF8A847A)
val Border = Color(0xFFEDE8E0)
val Accent = Color(0xFFE8923A)
val Accent2 = Color(0xFFF0C14A)
val Success = Color(0xFF3DAA5C)
val Error = Color(0xFFD4533A)
val AccentSoft = Color(0x1FE8923A)

private val LightColors = lightColorScheme(
    primary = Accent,
    onPrimary = Color.White,
    secondary = Accent2,
    background = Bg,
    onBackground = Fg,
    surface = Surface,
    onSurface = Fg,
    outline = Border,
    error = Error,
)

private val Typography = Typography(
    displayLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.ExtraBold, fontSize = 32.sp, color = Fg),
    headlineLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.ExtraBold, fontSize = 26.sp, color = Fg),
    headlineMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Fg),
    titleMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Fg),
    bodyLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 15.sp, color = Fg),
    bodyMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 13.sp, color = Muted),
    labelSmall = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = Muted, letterSpacing = 0.8.sp),
)

@Composable
fun WordFlowTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography,
        content = content,
    )
}
