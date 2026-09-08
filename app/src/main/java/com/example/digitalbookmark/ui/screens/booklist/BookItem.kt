package com.example.digitalbookmark.ui.screens.booklist

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.digitalbookmark.R
import com.example.digitalbookmark.data.local.BookEntity
import com.example.digitalbookmark.viewmodel.BookViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun BookItem(
    book: BookEntity,
    viewModel: BookViewModel,
    navController: NavHostController,
    onInfoClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {

            val painter = rememberAsyncImagePainter(
                model = if (book.imageUri.isBlank()) R.drawable.placeholder else book.imageUri,
                placeholder = painterResource(R.drawable.placeholder),
                error = painterResource(R.drawable.placeholder)
            )

            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onInfoClick() }
            ) {
                Text(
                    text = book.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text("Page: ${book.pageNumber}")
                Text(
                    text = "Status: ${book.status}",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Column {
                IconButton(onClick = {
                    navController.navigate("edit/${book.id}")
                }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }

                IconButton(onClick = {
                    viewModel.deleteBook(book)
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}
