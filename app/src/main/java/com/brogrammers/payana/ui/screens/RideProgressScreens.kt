package com.brogrammers.payana.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.brogrammers.payana.data.RideMatch
import com.brogrammers.payana.data.RideRequest
import com.brogrammers.payana.data.SampleData
import com.brogrammers.payana.matching.FareCalculator
import com.brogrammers.payana.matching.MatchingEngine
import com.brogrammers.payana.ui.maskedCommuterLabel
import com.brogrammers.payana.ui.theme.SuccessGreen
import com.brogrammers.payana.ui.theme.TealPrimary

@Composable
fun AcceptedRideScreen(request: RideRequest, match: RideMatch, onTrack: () -> Unit, onHome: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("You’re all set", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Your request was accepted in this demo.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        DemoMapCard(showRoute = true)
        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                Text("Ride accepted", color = SuccessGreen, fontWeight = FontWeight.Bold)
                Text(maskedCommuterLabel(match.commuter.id), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Pickup: ${SampleData.stationName(request.startStationId)}")
                Text("Drop: ${SampleData.destinationName(request.destinationId)}")
                Text("Illustrative contribution: ${FareCalculator.formatRupees(match.fareSplit.perPersonPaise)}")
                Text("Simulated ride • Fictional commuter data", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Button(onClick = onTrack, Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)) { Text("View demo tracking") }
        OutlinedButton(onClick = onHome, Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) { Text("Back to home") }
    }
}

@Composable
fun TrackingScreen(request: RideRequest, match: RideMatch, onFinish: () -> Unit) {
    val transition = rememberInfiniteTransition(label = "tracking-progress")
    val progress by transition.animateFloat(0.28f, 0.72f, infiniteRepeatable(tween(2200), RepeatMode.Reverse), label = "route-progress")
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Your shared ride", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Demo tracking", color = MaterialTheme.colorScheme.onSurfaceVariant)
        DemoMapCard(showRoute = true)
        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("On the way", color = SuccessGreen, fontWeight = FontWeight.Bold)
                    Text("${(progress * 10).toInt() + 3} min", fontWeight = FontWeight.Bold)
                }
                Text("Your rider ${maskedCommuterLabel(match.commuter.id)} is approaching the pickup point.")
                Text("This moving route is illustrative and uses no live GPS.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Spacer(Modifier.weight(1f))
        Button(onClick = onFinish, Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)) { Text("End demo ride") }
    }
}
