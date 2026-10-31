package com.brogrammers.payana

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.brogrammers.payana.ai.MatchExplainer
import com.brogrammers.payana.ui.PayanaApp
import com.brogrammers.payana.ui.theme.PayanaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Key is injected at build time from local.properties.
        val explainer = MatchExplainer(apiKey = BuildConfig.GEMINI_API_KEY)

        setContent {
            PayanaTheme(darkTheme = false) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PayanaApp(explainer = explainer)
                }
            }
        }
    }
}
