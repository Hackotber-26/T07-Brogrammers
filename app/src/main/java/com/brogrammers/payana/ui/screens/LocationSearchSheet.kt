package com.brogrammers.payana.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.brogrammers.payana.ui.theme.TealPrimary

data class LocationOption(
    val id: String,
    val name: String,
    val detail: String,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSearchSheet(
    title: String,
    options: List<LocationOption>,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var query by remember { mutableStateOf("") }
    val filtered = remember(query, options) {
        if (query.isBlank()) options
        else options.filter { option ->
            option.name.contains(query, ignoreCase = true) ||
                option.detail.contains(query, ignoreCase = true)
        }
    }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(title, style = androidx.compose.material3.MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Search station or destination") },
                placeholder = { Text("Try Indiranagar or Cubbon Park") },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) { Text("×", color = TealPrimary) }
                    }
                },
            )
            HorizontalDivider()
            if (filtered.isEmpty()) {
                Text(
                    "No demo locations found. Try a station or landmark name.",
                    modifier = Modifier.padding(vertical = 24.dp),
                    color = Color.Gray,
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    items(filtered, key = { it.id }) { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(option.id) }
                                .padding(vertical = 13.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("●", color = TealPrimary)
                            Column(Modifier.padding(start = 14.dp)) {
                                Text(option.name, fontWeight = FontWeight.SemiBold)
                                Text(option.detail, color = Color.Gray, style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
            Text(
                "Demo catalogue only — searchable locations do not represent live GPS or complete Bengaluru coverage.",
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 18.dp),
            )
        }
    }
}
