package com.example.digitalbookmark.ui.screens.booklist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.digitalbookmark.data.local.BookEntity
import com.example.digitalbookmark.viewmodel.BookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookListScreen(navController: NavHostController, viewModel: BookViewModel) {

    val booksState = viewModel.books.collectAsState()
    val books: List<BookEntity> = booksState.value

    val tabs = listOf("All", "Reading", "Completed", "Dropped", "On Hold", "Plan to Read")
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    val filtered = remember(books, selectedTabIndex, searchQuery) {
        val statusFilter = tabs[selectedTabIndex].trim()
        books.filter { book ->
            val matchesTab = statusFilter == "All" ||
                    book.status.trim().equals(statusFilter, ignoreCase = true)

            val matchesSearch = searchQuery.text.isBlank() ||
                    book.title.contains(searchQuery.text, ignoreCase = true)

            matchesTab && matchesSearch
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(12.dp)
        ) {

            PrimaryScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = 0.dp
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search by title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(filtered, key = { it.id }) { book ->
                    BookItem(
                        book = book,
                        viewModel = viewModel,
                        navController = navController,
                        onInfoClick = {}
                    )
                }

                if (filtered.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillParentMaxSize()
                                .padding(24.dp),
                            contentAlignment = androidx.compose.ui.Alignment.Center
                        ) {
                            Text("No books found")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { navController.navigate("add") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add New Book")
            }
        }
    }
}
