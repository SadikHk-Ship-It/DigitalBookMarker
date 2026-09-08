package com.example.digitalbookmark.ui.screens.booklist

import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.digitalbookmark.R
import com.example.digitalbookmark.data.local.BookEntity
import com.example.digitalbookmark.viewmodel.BookViewModel
import java.io.File

@Composable
fun EditBookScreen(
    navController: NavHostController,
    viewModel: BookViewModel,
    bookId: Int
) {
    val context = LocalContext.current

    // FIX: Make book non-null
    val book = viewModel.getBookById(bookId)
    if (book == null) {
        Text("Book not found")
        return
    }

    var title by remember { mutableStateOf(book.title) }
    var pageNumber by remember { mutableStateOf(book.pageNumber.toString()) }
    var review by remember { mutableStateOf(book.review) }
    var rating by remember { mutableStateOf(book.rating.toString()) }
    var status by remember { mutableStateOf(book.status) }
    var imageUri by remember { mutableStateOf(book.imageUri.toUri()) }

    // Create file for camera output
    val imageFile = remember {
        File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "photo_${System.currentTimeMillis()}.jpg"
        )
    }

    val cameraUri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        imageFile
    )

    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) imageUri = uri
    }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) imageUri = cameraUri
    }

    // Runtime permission (Android 13+)
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            cameraLauncher.launch(cameraUri)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Image preview
        Image(
            painter = rememberAsyncImagePainter(
                model = imageUri
            ),
            contentDescription = null,
            modifier = Modifier
                .size(140.dp)
                .clip(RoundedCornerShape(12.dp))
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = { galleryLauncher.launch("image/*") }) {
            Text("Pick Image From Gallery")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            } else {
                cameraLauncher.launch(cameraUri)
            }
        }) {
            Text("Take Photo")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = pageNumber,
            onValueChange = { pageNumber = it },
            label = { Text("Page Number") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = review,
            onValueChange = { review = it },
            label = { Text("Review") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 5
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = rating,
            onValueChange = { rating = it },
            label = { Text("Rating (1–10)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        val statuses = listOf("Reading", "Completed", "On Hold", "Dropped", "Plan to Read")
        var expanded by remember { mutableStateOf(false) }

        Box {
            OutlinedButton(onClick = { expanded = true }) {
                Text(status)
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                statuses.forEach {
                    DropdownMenuItem(
                        text = { Text(it) },
                        onClick = {
                            status = it
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val updatedBook = BookEntity(
                    id = book.id,
                    title = title,
                    pageNumber = pageNumber.toIntOrNull() ?: 0,
                    review = review,
                    rating = rating.toIntOrNull() ?: 0,
                    imageUri = imageUri.toString(),
                    status = status
                )

                viewModel.updateBook(updatedBook)
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Changes")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Text("Back")
        }

    }
}
