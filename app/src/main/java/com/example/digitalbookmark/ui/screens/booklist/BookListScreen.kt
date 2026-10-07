package com.example.digitalbookmark.ui.screens.booklist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.digitalbookmark.data.local.BookEntity
import com.example.digitalbookmark.ui.theme.Spacing
import com.example.digitalbookmark.viewmodel.BookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookListScreen(navController: NavHostController, viewModel: BookViewModel) {
    val books by viewModel.books.collectAsState()

    val tabs = remember { listOf("All") + bookStatuses }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var bookToDelete by remember { mutableStateOf<BookEntity?>(null) }

    val filtered = remember(books, selectedTabIndex, searchQuery) {
        val statusFilter = tabs[selectedTabIndex]
        books.filter { book ->
            val matchesTab = statusFilter == "All" ||
                    book.status.trim().equals(statusFilter, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                    book.title.contains(searchQuery, ignoreCase = true)
            matchesTab && matchesSearch
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("My Library") }) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate("add") },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add book") }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by title") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.md)
            )

            PrimaryScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = Spacing.md,
                modifier = Modifier.padding(top = Spacing.sm)
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            if (filtered.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(Spacing.lg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (books.isEmpty()) "Your library is empty.\nTap “Add book” to start."
                        else "No books found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    // Ekstra bunn-padding så siste kort ikke havner bak FAB
                    contentPadding = PaddingValues(
                        start = Spacing.md, end = Spacing.md,
                        top = Spacing.md, bottom = 88.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    items(filtered, key = { it.id }) { book ->
                        BookItem(
                            book = book,
                            onClick = { navController.navigate("edit/${book.id}") },
                            onDelete = { bookToDelete = book }
                        )
                    }
                }
            }
        }
    }

    bookToDelete?.let { book ->
        AlertDialog(
            onDismissRequest = { bookToDelete = null },
            title = { Text("Delete book?") },
            text = { Text("“${book.title}” will be removed from your library.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteBook(book)
                    bookToDelete = null
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { bookToDelete = null }) { Text("Cancel") }
            }
        )
    }
}