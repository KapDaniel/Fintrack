package com.fintrack.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.fintrack.app.ui.navigation.FinTrackApp
import com.fintrack.app.ui.theme.FinTrackTheme

/**
 * Единственная Activity приложения: вся навигация живёт внутри Compose.
 *
 * [enableEdgeToEdge] включает отрисовку под системными панелями — отступы
 * расставлены самими экранами (statusBarsPadding в шапке,
 * navigationBarsPadding в нижней навигации, imePadding в форме).
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            FinTrackTheme {
                FinTrackApp()
            }
        }
    }
}
