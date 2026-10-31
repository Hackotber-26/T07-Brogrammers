package com.brogrammers.payana.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.brogrammers.payana.data.RideMatch
import com.brogrammers.payana.data.RideRequest
import com.brogrammers.payana.data.SampleData
import com.brogrammers.payana.matching.FareCalculator
import com.brogrammers.payana.matching.MatchingEngine
import com.brogrammers.payana.ui.theme.SuccessGreen
import com.brogrammers.payana.ui.maskedCommuterLabel

@Composable
fun MatchDetailScreen(
    request: RideRequest,
    match: RideMatch,
    aiExplanation: String,
    explanationFromAi: Boolean,
    requestConfirmed: Boolean,
    onConfirmRequest: () -> Unit,
    onBack: () -> Unit,
    onHome: () -> Unit,
) {
    val c = match.commuter
    val fare = match.fareSplit

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            if (requestConfirmed) "Request confirmed (demo)" else "Match details",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = if (requestConfirmed) SuccessGreen else MaterialTheme.colorScheme.onBackground,
        )

        DemoMapCard(showRoute = true)

        Text(
            "Your ride summary",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(1.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(maskedCommuterLabel(c.id), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                Text("Route compatibility", style = MaterialTheme.typography.titleMedium)
                Text(
                    "You: ${SampleData.stationName(request.startStationId)} → ${SampleData.destinationName(request.destinationId)} at ${MatchingEngine.formatTime(request.departureMinutes)}",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    "Them: ${SampleData.stationName(c.startStationId)} → ${SampleData.destinationName(c.destinationId)} at ${MatchingEngine.formatTime(c.departureMinutes)}",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    "Estimated additional detour: ~${match.estimatedDetourMinutes} min (simplified demo heuristic, not live navigation).",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Illustrative fare split", style = MaterialTheme.typography.titleMedium)
                Text("Assumed total ride cost: ${FareCalculator.formatRupees(fare.totalFarePaise)}")
                Text("Passengers after you join: ${fare.passengerCount}")
                Text(
                    "Estimated contribution per person: ${FareCalculator.formatRupees(fare.perPersonPaise)}",
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    "*Not a live auto-rickshaw quote — fictional demo math in paise.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(1.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    if (explanationFromAi) "AI match explanation" else "Match explanation (local fallback)",
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(aiExplanation, style = MaterialTheme.typography.bodyLarge)
            }
        }

        Text(
            "Disclaimer: Fares and commuters are fictional demonstration data. Payana does not provide real-time ride availability, live traffic, actual fares, or verified identities.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        if (!requestConfirmed) {
            Button(
                onClick = onConfirmRequest,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
            ) {
                Text("Confirm join (local demo)")
            }
        } else {
            Text(
                "Saved locally for this demo session. No real person was contacted.",
                style = MaterialTheme.typography.bodyMedium,
                color = SuccessGreen,
            )
        }

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
        ) { Text("Back to results") }

        OutlinedButton(
            onClick = onHome,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
        ) { Text("New search") }

        Spacer(Modifier.height(16.dp))
    }
}
