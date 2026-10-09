package com.example.digitalbookmark.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.digitalbookmark.data.local.BookEntity
import com.example.digitalbookmark.data.local.BookType
import com.example.digitalbookmark.data.local.parseTimestamp
import com.example.digitalbookmark.ui.screens.booklist.StatusDropdown
import com.example.digitalbookmark.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypeDropdown(
    selected: BookType,
    onSelected: (BookType) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selected.label,
            onValueChange = {},
            readOnly = true,
            label = { Text("Type") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            BookType.values().forEach { t ->
                DropdownMenuItem(
                    text = { Text(t.label) },
                    onClick = { onSelected(t); expanded = false }
                )
            }
        }
    }
}

@Composable
fun BookForm(
    initial: BookEntity?,
    saveLabel: String,
    onSave: (BookEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember(initial?.id) { mutableStateOf(initial?.title ?: "") }
    var pageNumber by remember(initial?.id) {
        mutableStateOf(initial?.pageNumber?.takeIf { it > 0 }?.toString() ?: "")
    }
    var review by remember(initial?.id) { mutableStateOf(initial?.review ?: "") }
    var rating by remember(initial?.id) {
        mutableStateOf(initial?.rating?.takeIf { it > 0 }?.toString() ?: "")
    }
    var status by remember(initial?.id) { mutableStateOf(initial?.status ?: "Reading") }
    var imageUri by remember(initial?.id) { mutableStateOf(initial?.imageUri ?: "") }
    var type by remember(initial?.id) { mutableStateOf(initial?.bookType ?: BookType.BOOK) }

    val startSeconds = initial?.timestampSeconds ?: 0L
    var hours by remember(initial?.id) {
        mutableStateOf((startSeconds / 3600).takeIf { it > 0 }?.toString() ?: "")
    }
    var minutes by remember(initial?.id) {
        mutableStateOf(((startSeconds % 3600) / 60).takeIf { it > 0 }?.toString() ?: "")
    }
    var seconds by remember(initial?.id) {
        mutableStateOf((startSeconds % 60).takeIf { it > 0 }?.toString() ?: "")
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ImagePickerSection(imageUri = imageUri, onImageChange = { imageUri = it })

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        TypeDropdown(
            selected = type,
            onSelected = { type = it },
            modifier = Modifier.fillMaxWidth()
        )

        if (type.usesTimestamp) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = hours,
                    onValueChange = { hours = it.filter(Char::isDigit).take(3) },
                    label = { Text("Hours") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = minutes,
                    onValueChange = { minutes = it.filter(Char::isDigit).take(2) },
                    label = { Text("Min") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = seconds,
                    onValueChange = { seconds = it.filter(Char::isDigit).take(2) },
                    label = { Text("Sec") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            OutlinedTextField(
                value = pageNumber,
                onValueChange = { pageNumber = it.filter(Char::isDigit).take(6) },
                label = { Text("Page number") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        }

        OutlinedTextField(
            value = rating,
            onValueChange = { rating = it.filter(Char::isDigit).take(2) },
            label = { Text("Rating") },
            supportingText = { Text("1–10") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        StatusDropdown(
            selectedStatus = status,
            onStatusSelected = { status = it },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = review,
            onValueChange = { review = it },
            label = { Text("Review") },
            minLines = 3,
            maxLines = 8,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            enabled = title.isNotBlank(),
            onClick = {
                val pages = pageNumber.toIntOrNull() ?: 0
                val stars = (rating.toIntOrNull() ?: 0).coerceIn(0, 10)
                val timeSeconds = parseTimestamp(hours, minutes, seconds)
                val book = initial?.copy(
                    title = title.trim(),
                    pageNumber = pages,
                    review = review,
                    rating = stars,
                    imageUri = imageUri,
                    status = status,
                    type = type.name,
                    timestampSeconds = timeSeconds
                ) ?: BookEntity(
                    title = title.trim(),
                    pageNumber = pages,
                    review = review,
                    rating = stars,
                    imageUri = imageUri,
                    status = status,
                    type = type.name,
                    timestampSeconds = timeSeconds
                )
                onSave(book)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(saveLabel)
        }
    }
}