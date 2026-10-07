package com.example.digitalbookmark.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import com.example.digitalbookmark.data.local.BookEntity
import com.example.digitalbookmark.ui.screens.booklist.StatusDropdown
import com.example.digitalbookmark.ui.theme.Spacing


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

        OutlinedTextField(
            value = pageNumber,
            onValueChange = { pageNumber = it.filter(Char::isDigit).take(6) },
            label = { Text("Page number") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

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
                val book = initial?.copy(
                    title = title.trim(),
                    pageNumber = pages,
                    review = review,
                    rating = stars,
                    imageUri = imageUri,
                    status = status
                ) ?: BookEntity(
                    title = title.trim(),
                    pageNumber = pages,
                    review = review,
                    rating = stars,
                    imageUri = imageUri,
                    status = status
                )
                onSave(book)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(saveLabel)
        }
    }
}