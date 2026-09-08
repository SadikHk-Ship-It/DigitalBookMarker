package com.example.digitalbookmark.ui.screens.addbook

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.digitalbookmark.R
import com.example.digitalbookmark.data.local.BookEntity
import com.example.digitalbookmark.ui.screens.booklist.StatusDropdown
import com.example.digitalbookmark.viewmodel.BookViewModel
import java.io.File


@Composable
fun AddBookScreen(
    navController: NavController,
    viewModel: BookViewModel
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var pageNumber by remember { mutableStateOf("") }
    var review by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Reading") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Create a file for camera output
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
                model = imageUri ?: R.drawable.placeholder
            ),
            contentDescription = null,
            modifier = Modifier
                .size(140.dp)
                .clip(RoundedCornerShape(12.dp))
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Pick from gallery
        Button(onClick = { galleryLauncher.launch("image/*") }) {
            Text("Pick Image From Gallery")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Take photo
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

        // Title
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Page number
        OutlinedTextField(
            value = pageNumber,
            onValueChange = { pageNumber = it },
            label = { Text("Page Number") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Review
        OutlinedTextField(
            value = review,
            onValueChange = { review = it },
            label = { Text("Review") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Rating
        OutlinedTextField(
            value = rating,
            onValueChange = { rating = it },
            label = { Text("Rating (1–10)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Status dropdown
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

        // Save button
        Button(
            onClick = {
                val book = BookEntity(
                    title = title,
                    pageNumber = pageNumber.toIntOrNull() ?: 0,
                    review = review,
                    rating = rating.toIntOrNull() ?: 0,
                    imageUri = imageUri?.toString() ?: "",
                    status = status
                )

                viewModel.addBook(book)
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Book")
        }
    }
}
