package com.example.digitalbookmark.ui.screens.booklist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.digitalbookmark.data.local.BookEntity
import com.example.digitalbookmark.viewmodel.BookViewModel

@Composable
fun EditSheetContent(
    book: BookEntity,
    viewModel: BookViewModel,
    onClose: () -> Unit
) {
    var title by remember { mutableStateOf(book.title) }
    var page by remember { mutableStateOf(book.pageNumber.toString()) }

    val statuses = listOf("Reading", "Completed", "Dropped", "On Hold")
    var expanded by remember { mutableStateOf(false) }
    var selectedStatus by remember { mutableStateOf(book.status) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        Text("Edit Book")

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = page,
            onValueChange = { page = it },
            label = { Text("Page Number") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Status dropdown
        Box {
            OutlinedTextField(
                value = selectedStatus,
                onValueChange = {},
                label = { Text("Status") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true },   // whole field clickable
                readOnly = true
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                statuses.forEach { status ->
                    DropdownMenuItem(
                        text = { Text(status) },
                        onClick = {
                            selectedStatus = status
                            expanded = false
                        }
                    )
                }
            }
        }


        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                val updated = book.copy(
                    title = title,
                    pageNumber = page.toIntOrNull() ?: 0,
                    status = selectedStatus
                )
                viewModel.updateBook(updated)
                onClose()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }
    }
}
