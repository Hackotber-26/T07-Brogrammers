package com.brogrammers.payana.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.brogrammers.payana.ui.theme.TealContainer
import com.brogrammers.payana.ui.theme.TealPrimary

@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    var page by remember { mutableIntStateOf(0) }
    val content = listOf(
        "Your metro journey, connected." to "Payana helps commuters discover compatible people for shared last-mile journeys.",
        "Share the ride. Split the cost." to "Match by route, departure time, seats, and a configurable demo detour limit.",
        "Your journey. Your privacy." to "Names and contact details stay masked in this prototype. No real person is contacted.",
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text("PAYANA", color = TealPrimary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(28.dp))
            AnimatedContent(targetState = page, label = "onboarding-page") { selected ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    DemoMapCard(
                        modifier = Modifier.fillMaxWidth(),
                        showRoute = selected != 1,
                    )
                    Spacer(Modifier.height(28.dp))
                    Text(content[selected].first, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(10.dp))
                    Text(content[selected].second, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                }
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                repeat(content.size) { index ->
                    androidx.compose.foundation.layout.Box(
                        Modifier.padding(horizontal = 4.dp).size(if (index == page) 24.dp else 8.dp, 8.dp).clip(CircleShape).background(if (index == page) TealPrimary else TealContainer),
                    )
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onFinished, modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp)) { Text("Skip") }
                Button(
                    onClick = { if (page == content.lastIndex) onFinished() else page++ },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                ) { Text(if (page == content.lastIndex) "Get Started" else "Next") }
            }
        }
    }
}
