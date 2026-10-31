package com.brogrammers.payana.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.brogrammers.payana.data.MatchPreferences
import com.brogrammers.payana.data.RideRequest
import com.brogrammers.payana.data.SampleData
import com.brogrammers.payana.matching.MatchingEngine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onFindRide: (RideRequest) -> Unit,
) {
    var startStationId by remember { mutableStateOf(SampleData.stations.first().id) }
    var destinationId by remember { mutableStateOf(SampleData.destinations.first().id) }
    var departureMinutes by remember { mutableIntStateOf(9 * 60) }
    var passengers by remember { mutableIntStateOf(1) }
    var maxDetour by remember { mutableFloatStateOf(15f) }
    var womenOnly by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val timeOptions = remember {
        (7 * 60..21 * 60 step 15).map { it to MatchingEngine.formatTime(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        BrandHeader()
        Spacer(Modifier.height(20.dp))
        DemoMapCard()
        Spacer(Modifier.height(18.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    "Find a shared last-mile ride",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )

                DropdownField(
                    label = "Starting metro station",
                    options = SampleData.stations.map { it.id to it.name },
                    selectedId = startStationId,
                    onSelect = { startStationId = it },
                )

                DropdownField(
                    label = "Destination / landmark",
                    options = SampleData.destinations.map { it.id to it.name },
                    selectedId = destinationId,
                    onSelect = { destinationId = it },
                )

                DropdownField(
                    label = "Departure time",
                    options = timeOptions.map { it.first.toString() to it.second },
                    selectedId = departureMinutes.toString(),
                    onSelect = { departureMinutes = it.toInt() },
                )

                Text("Passengers: $passengers", style = MaterialTheme.typography.bodyMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    (1..3).forEach { n ->
                        FilterChip(
                            selected = passengers == n,
                            onClick = { passengers = n },
                            label = { Text("$n") },
                        )
                    }
                }

                Text(
                    "Max detour: ${maxDetour.toInt()} min (demo estimate)",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Slider(
                    value = maxDetour,
                    onValueChange = { maxDetour = it },
                    valueRange = 5f..30f,
                    steps = 4,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("Women-only group", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Uses explicit demo attributes only",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Switch(checked = womenOnly, onCheckedChange = { womenOnly = it })
                }

                error?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                Button(
                    onClick = {
                        if (startStationId.isBlank() || destinationId.isBlank()) {
                            error = "Please select a station and destination."
                            return@Button
                        }
                        error = null
                        onFindRide(
                            RideRequest(
                                startStationId = startStationId,
                                destinationId = destinationId,
                                departureMinutes = departureMinutes,
                                passengerCount = passengers,
                                preferences = MatchPreferences(
                                    maxDetourMinutes = maxDetour.toInt(),
                                    womenOnly = womenOnly,
                                ),
                            ),
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
                ) {
                    Text("Find My Ride", style = MaterialTheme.typography.labelLarge)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Text(
            "Demo note: matches and fares are fictional illustrative data for a hackathon prototype — not live rides or real prices.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun BrandHeader() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center,
        ) {
            Text("P", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.size(12.dp))
        Column {
            Text(
                "Payana",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
            Text(
                "Your metro journey, connected.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownField(
    label: String,
    options: List<Pair<String, String>>,
    selectedId: String,
    onSelect: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = options.firstOrNull { it.first == selectedId }?.second ?: ""

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
            shape = RoundedCornerShape(12.dp),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { (id, name) ->
                DropdownMenuItem(
                    text = { Text(name) },
                    onClick = {
                        onSelect(id)
                        expanded = false
                    },
                )
            }
        }
    }
}
