package com.brogrammers.payana.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Surface
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
import com.brogrammers.payana.ui.theme.TealPrimary
import java.util.Calendar
import kotlin.math.abs

private enum class LocationType { Start, Destination }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LegacyHomeScreen(onFindRide: (RideRequest) -> Unit) {
    var startStationId by remember { mutableStateOf(SampleData.stations.first().id) }
    var destinationId by remember { mutableStateOf(SampleData.destinations.first().id) }
    val currentDeparture = remember {
        val now = Calendar.getInstance()
        val minutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
        ((minutes + 14) / 15 * 15) % (24 * 60)
    }
    val demoDefaultDeparture = remember(startStationId, destinationId) {
        SampleData.seedCommuters.firstOrNull {
            it.startStationId == startStationId && it.destinationId == destinationId
        }?.departureMinutes ?: (9 * 60)
    }
    var departureMinutes by remember(startStationId, destinationId) { mutableIntStateOf(demoDefaultDeparture) }
    var passengers by remember { mutableIntStateOf(1) }
    var maxDetour by remember { mutableFloatStateOf(15f) }
    var womenOnly by remember { mutableStateOf(false) }
    var activeLocation by remember { mutableStateOf<LocationType?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    val timeOptions = remember(currentDeparture) {
        (listOf(currentDeparture) + SampleData.seedCommuters.map { it.departureMinutes } + (0 until 24 * 60 step 15).toList())
            .distinct().sorted().map { it to MatchingEngine.formatTime(it) }
    }
    val stationOptions = remember {
        SampleData.stations.map { LocationOption(it.id, it.name, "Bengaluru Metro station • ${it.line}") }
    }
    val destinationOptions = remember {
        SampleData.destinations.map { LocationOption(it.id, it.name, "Demo landmark / last-mile destination") }
    }

    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            BrandHeader()
            Spacer(Modifier.height(24.dp))
            Text("Where are we heading?", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("Share the last mile from Bengaluru Metro.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(16.dp))
            DemoMapCard(height = 360.dp)
            Spacer(Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Plan your shared ride", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    LocationField(
                        label = "FROM",
                        value = SampleData.stationName(startStationId),
                        hint = "Choose pickup location",
                        markerColor = TealPrimary,
                        onClick = { activeLocation = LocationType.Start },
                    )
                    LocationField(
                        label = "TO",
                        value = SampleData.destinationName(destinationId),
                        hint = "Where do you want to go?",
                        markerColor = MaterialTheme.colorScheme.error,
                        onClick = { activeLocation = LocationType.Destination },
                    )
                    DropdownField(
                        label = "Departure time",
                        options = timeOptions.map { it.first.toString() to it.second },
                        selectedId = departureMinutes.toString(),
                        onSelect = { departureMinutes = it.toInt() },
                    )
                    Text("Passengers", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        (1..3).forEach { n -> FilterChip(selected = passengers == n, onClick = { passengers = n }, label = { Text("$n") }) }
                    }
                    Text("Maximum detour: ${maxDetour.toInt()} minutes", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Slider(value = maxDetour, onValueChange = { maxDetour = it }, valueRange = 5f..30f, steps = 4)
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(Modifier.weight(1f)) {
                            Text("Women-only matching", fontWeight = FontWeight.SemiBold)
                            Text("Uses explicit demo attributes only", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(checked = womenOnly, onCheckedChange = { womenOnly = it })
                    }
                    error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                    Button(
                        onClick = {
                            if (startStationId.isBlank() || destinationId.isBlank()) {
                                error = "Please choose a pickup and destination."
                            } else {
                                error = null
                                onFindRide(RideRequest(startStationId, destinationId, departureMinutes, passengers, MatchPreferences(maxDetour.toInt(), womenOnly)))
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    ) { Text("Find My Ride", style = MaterialTheme.typography.labelLarge) }
                }
            }
            Spacer(Modifier.height(14.dp))
            Text("Privacy note: commuter identities are masked until a simulated request is confirmed.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(24.dp))
        }

        activeLocation?.let { type ->
            LocationSearchSheet(
                title = if (type == LocationType.Start) "Choose pickup location" else "Choose destination",
                options = if (type == LocationType.Start) stationOptions else destinationOptions,
                onDismiss = { activeLocation = null },
                onSelect = { selected ->
                    if (type == LocationType.Start) startStationId = selected else destinationId = selected
                    activeLocation = null
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onFindRide: (RideRequest) -> Unit) {
    var startStationId by remember { mutableStateOf(SampleData.stations.first().id) }
    var destinationId by remember { mutableStateOf(SampleData.destinations.first().id) }
    val currentDeparture = remember {
        val now = Calendar.getInstance()
        val minutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
        ((minutes + 14) / 15 * 15) % (24 * 60)
    }
    val demoDefaultDeparture = remember(startStationId, destinationId) {
        SampleData.seedCommuters.firstOrNull {
            it.startStationId == startStationId && it.destinationId == destinationId
        }?.departureMinutes ?: (9 * 60)
    }
    var departureMinutes by remember(startStationId, destinationId) { mutableIntStateOf(demoDefaultDeparture) }
    var passengers by remember { mutableIntStateOf(1) }
    var maxDetour by remember { mutableFloatStateOf(15f) }
    var womenOnly by remember { mutableStateOf(false) }
    var activeLocation by remember { mutableStateOf<LocationType?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    val timeOptions = remember(currentDeparture) {
        (listOf(currentDeparture) + SampleData.seedCommuters.map { it.departureMinutes } + (0 until 24 * 60 step 15).toList())
            .distinct().sorted().map { it to MatchingEngine.formatTime(it) }
    }
    val stationOptions = remember { SampleData.stations.map { LocationOption(it.id, it.name, "Bengaluru Metro station • ${it.line}") } }
    val destinationOptions = remember { SampleData.destinations.map { LocationOption(it.id, it.name, "Demo landmark / last-mile destination") } }

    Box(Modifier.fillMaxSize()) {
        DemoMapCard(
            modifier = Modifier.fillMaxSize(),
            height = null,
            startStationId = startStationId,
            destinationId = destinationId,
        )

        Surface(
            modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = .96f),
            shadowElevation = 6.dp,
        ) { BrandHeader() }

        Card(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(14.dp).heightIn(max = 450.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = .97f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        ) {
            Column(
                Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(11.dp),
            ) {
                Box(Modifier.fillMaxWidth().padding(bottom = 2.dp), contentAlignment = Alignment.Center) {
                    Box(Modifier.size(38.dp, 4.dp).clip(RoundedCornerShape(4.dp)).background(MaterialTheme.colorScheme.outlineVariant))
                }
                Text("Where are we heading?", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Share the last mile from Bengaluru Metro.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                LocationField("FROM", SampleData.stationName(startStationId), "Choose pickup location", TealPrimary) { activeLocation = LocationType.Start }
                LocationField("TO", SampleData.destinationName(destinationId), "Where do you want to go?", MaterialTheme.colorScheme.error) { activeLocation = LocationType.Destination }
                DropdownField("Departure time", timeOptions.map { it.first.toString() to it.second }, departureMinutes.toString()) { departureMinutes = it.toInt() }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Passengers", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) { (1..3).forEach { n -> FilterChip(selected = passengers == n, onClick = { passengers = n }, label = { Text("$n") }) } }
                }
                Text("Maximum detour: ${maxDetour.toInt()} minutes", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Slider(value = maxDetour, onValueChange = { maxDetour = it }, valueRange = 5f..30f, steps = 4)
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(Modifier.weight(1f)) { Text("Women-only matching", fontWeight = FontWeight.SemiBold); Text("Explicit demo preference", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                    Switch(checked = womenOnly, onCheckedChange = { womenOnly = it })
                }
                error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                Button(
                    onClick = {
                        if (startStationId.isBlank() || destinationId.isBlank()) error = "Please choose a pickup and destination."
                        else { error = null; onFindRide(RideRequest(startStationId, destinationId, departureMinutes, passengers, MatchPreferences(maxDetour.toInt(), womenOnly))) }
                    },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                ) { Text("Find My Ride") }
            }
        }

        activeLocation?.let { type ->
            LocationSearchSheet(
                title = if (type == LocationType.Start) "Choose pickup location" else "Choose destination",
                options = if (type == LocationType.Start) stationOptions else destinationOptions,
                onDismiss = { activeLocation = null },
                onSelect = { selected -> if (type == LocationType.Start) startStationId = selected else destinationId = selected; activeLocation = null },
            )
        }
    }
}

@Composable
private fun BrandHeader() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(46.dp).clip(CircleShape).background(TealPrimary), contentAlignment = Alignment.Center) {
            Text("P", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.size(12.dp))
        Column {
            Text("Payana", style = MaterialTheme.typography.headlineSmall, color = TealPrimary, fontWeight = FontWeight.Bold)
            Text("Metro journeys, connected.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun LocationField(label: String, value: String, hint: String, markerColor: androidx.compose.ui.graphics.Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surfaceVariant).clickable(onClick = onClick).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(12.dp).clip(CircleShape).background(markerColor))
        Column(Modifier.padding(start = 12.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
            Text(value.ifBlank { hint }, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.weight(1f))
        Text("›", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownField(label: String, options: List<Pair<String, String>>, selectedId: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = options.firstOrNull { it.first == selectedId }?.second ?: ""
    Box(Modifier.fillMaxWidth().clickable { expanded = true }) {
        OutlinedTextField(
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            enabled = false,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
        )
    }
    if (expanded) {
        AlertDialog(
            onDismissRequest = { expanded = false },
            title = { Text(label) },
            text = {
                LazyColumn(modifier = Modifier.heightIn(max = 360.dp)) {
                    items(options) { (id, name) ->
                        DropdownMenuItem(
                            text = { Text(name) },
                            onClick = { onSelect(id); expanded = false },
                        )
                    }
                }
            },
            confirmButton = {},
        )
    }
}
