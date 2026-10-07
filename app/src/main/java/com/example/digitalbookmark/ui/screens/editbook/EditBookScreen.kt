package com.example.digitalbookmark.ui.screens.editbook

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.digitalbookmark.ui.components.AppTopBar
import com.example.digitalbookmark.ui.components.BookForm
import com.example.digitalbookmark.viewmodel.BookViewModel

@Composable
fun EditBookScreen(
    navController: NavHostController,
    viewModel: BookViewModel,
    bookId: Int
) {
    val book = viewModel.getBookById(bookId)

    Scaffold(
        topBar = { AppTopBar(title = "Edit book", onBack = { navController.popBackStack() }) }
    ) { padding ->
        if (book == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Book not found")
            }
        } else {
            BookForm(
                initial = book,
                saveLabel = "Save changes",
                onSave = { updated ->
                    viewModel.updateBook(updated)
                    navController.popBackStack()
                },
                modifier = Modifier.padding(padding)
            )
        }
    }
}