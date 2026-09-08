package app.wordflow.trainer

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import app.wordflow.trainer.ui.WordFlowApp
import app.wordflow.trainer.ui.theme.WordFlowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        CyloneIdAuth.handleRedirect(intent?.data)
        enableEdgeToEdge()
        setContent {
            WordFlowTheme {
                WordFlowApp()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        CyloneIdAuth.handleRedirect(intent?.data)
    }
}
