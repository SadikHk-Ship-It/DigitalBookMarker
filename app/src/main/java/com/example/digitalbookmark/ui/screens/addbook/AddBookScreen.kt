package com.example.digitalbookmark.ui.screens.addbook

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.digitalbookmark.ui.components.AppTopBar
import com.example.digitalbookmark.ui.components.BookForm
import com.example.digitalbookmark.viewmodel.BookViewModel

@Composable
fun AddBookScreen(
    navController: NavController,
    viewModel: BookViewModel
) {
    Scaffold(
        topBar = { AppTopBar(title = "Add book", onBack = { navController.popBackStack() }) }
    ) { padding ->
        BookForm(
            initial = null,
            saveLabel = "Save book",
            onSave = { book ->
                viewModel.addBook(book)
                navController.popBackStack()
            },
            modifier = Modifier.padding(padding)
        )
    }
}