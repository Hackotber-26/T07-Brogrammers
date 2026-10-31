package com.brogrammers.payana.ui.screens

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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.brogrammers.payana.ui.theme.TealPrimary

@Composable
fun ProfileSetupScreen(onComplete: (String, String, Boolean) -> Unit) {
    var name by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Prefer not to say") }
    var shareName by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Make your ride feel personal.", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("A few choices help us shape your demo experience. You can stay anonymous.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it.take(28) },
            label = { Text("Your display name (optional)") },
            placeholder = { Text("e.g. Ananya") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
        )
        Spacer(Modifier.height(18.dp))
        Text("Gender preference", style = MaterialTheme.typography.labelLarge)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Woman", "Man", "Prefer not to say").forEach { option ->
                FilterChip(selected = gender == option, onClick = { gender = option }, label = { Text(option) })
            }
        }
        Spacer(Modifier.height(18.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(Modifier.weight(1f)) {
                Text("Show my name after acceptance", fontWeight = FontWeight.SemiBold)
                Text("Off by default. Names stay masked before a simulated acceptance.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(checked = shareName, onCheckedChange = { shareName = it })
        }
        Spacer(Modifier.height(28.dp))
        Button(
            onClick = { onComplete(name.ifBlank { "Payana commuter" }, gender, shareName) },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
        ) { Text("Continue") }
    }
}
