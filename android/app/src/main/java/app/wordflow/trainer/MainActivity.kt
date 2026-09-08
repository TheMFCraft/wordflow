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
        handleAuth(intent)
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
        handleAuth(intent)
    }

    private fun handleAuth(intent: Intent?) {
        CyloneIdAuth.handleRedirect(intent?.data)
    }
}
