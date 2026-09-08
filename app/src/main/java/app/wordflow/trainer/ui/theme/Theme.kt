package app.wordflow.trainer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

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
val CyloneGrey = Color(0xFF333333)

// Dark Palette
val BgDark = Color(0xFF1C1B19)
val SurfaceDark = Color(0xFF2C2926)
val FgDark = Color(0xFFE6E1DD)
val MutedDark = Color(0xFF948F85)
val BorderDark = Color(0xFF3D3935)
val AccentSoftDark = Color(0x26E8923A)

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

private val DarkColors = darkColorScheme(
    primary = Accent,
    onPrimary = Color.White,
    secondary = Accent2,
    background = BgDark,
    onBackground = FgDark,
    surface = SurfaceDark,
    onSurface = FgDark,
    outline = BorderDark,
    error = Error,
)

private val Typography = Typography(
    displayLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.ExtraBold, fontSize = 32.sp),
    headlineLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.ExtraBold, fontSize = 26.sp),
    headlineMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 20.sp),
    titleMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
    bodyLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 15.sp),
    bodyMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 13.sp),
    labelSmall = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, letterSpacing = 0.8.sp),
)

@Composable
fun WordFlowTheme(theme: String = "system", content: @Composable () -> Unit) {
    val dark = when (theme) {
        "dark" -> true
        "light" -> false
        else -> isSystemInDarkTheme()
    }
    
    MaterialTheme(
        colorScheme = if (dark) DarkColors else LightColors,
        typography = Typography,
        content = content,
    )
}
