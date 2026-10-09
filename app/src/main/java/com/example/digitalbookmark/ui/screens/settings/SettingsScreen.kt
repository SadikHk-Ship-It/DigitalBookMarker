package com.example.digitalbookmark.ui.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.digitalbookmark.ui.theme.ColorSlot
import com.example.digitalbookmark.ui.theme.ThemeMode
import com.example.digitalbookmark.ui.theme.colorFor
import com.example.digitalbookmark.ui.viewmodel.SettingsViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(vm: SettingsViewModel, onBack: () -> Unit) {
    val settings by vm.settings.collectAsState()
    val scheme = MaterialTheme.colorScheme
    var editing by remember { mutableStateOf<ColorSlot?>(null) }
    var showResetConfirm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            item {
                Text("Theme", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ThemeMode.entries.forEach { mode ->
                        FilterChip(
                            selected = settings.mode == mode,
                            onClick = { vm.setMode(mode) },
                            label = { Text(mode.label) }
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text("Colors", style = MaterialTheme.typography.titleMedium)
            }

            items(ColorSlot.entries) { slot ->
                val saved = settings.overrides[slot]
                val current = saved ?: scheme.colorFor(slot)
                val adjusted = saved != null && scheme.colorFor(slot) != saved
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(scheme.surfaceVariant)
                        .border(BorderStroke(1.dp, scheme.outline), RoundedCornerShape(16.dp))
                        .clickable { editing = slot }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(slot.label, color = scheme.onSurface)
                        if (adjusted) {
                            Text(
                                "Adjusted for readability",
                                style = MaterialTheme.typography.bodySmall,
                                color = scheme.onSurfaceVariant
                            )
                        }
                    }
                    Box(
                        Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(current)
                            .border(1.dp, scheme.outline, CircleShape)
                    )
                }
            }

            item {
                OutlinedButton(
                    onClick = { showResetConfirm = true },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Reset all colors") }
            }
        }
    }

    editing?.let { slot ->
        ColorPickerDialog(
            title = slot.label,
            initial = settings.overrides[slot] ?: scheme.colorFor(slot),
            isCustomized = slot in settings.overrides,
            onDismiss = { editing = null },
            onApply = { vm.setColor(slot, it); editing = null },
            onReset = { vm.resetColor(slot); editing = null }
        )
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text("Reset all colors?") },
            text = { Text("All your custom colors will be removed and the default theme will be restored.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.resetAll()
                    showResetConfirm = false
                }) { Text("Reset") }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun ColorPickerDialog(
    title: String,
    initial: Color,
    isCustomized: Boolean,
    onDismiss: () -> Unit,
    onApply: (Color) -> Unit,
    onReset: () -> Unit
) {
    var r by remember { mutableFloatStateOf(initial.red) }
    var g by remember { mutableFloatStateOf(initial.green) }
    var b by remember { mutableFloatStateOf(initial.blue) }
    val color = Color(r, g, b)
    val hex = "#%02X%02X%02X".format(
        (r * 255).roundToInt(), (g * 255).roundToInt(), (b * 255).roundToInt()
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(color)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                )
                Text(hex, style = MaterialTheme.typography.labelLarge)
                Text("Red");   Slider(value = r, onValueChange = { r = it })
                Text("Green"); Slider(value = g, onValueChange = { g = it })
                Text("Blue");  Slider(value = b, onValueChange = { b = it })
                if (isCustomized) {
                    TextButton(onClick = onReset) { Text("Reset this color") }
                }
            }
        },
        confirmButton = { TextButton(onClick = { onApply(color) }) { Text("Apply") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}