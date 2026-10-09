package com.example.digitalbookmark.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.digitalbookmark.ui.screens.addbook.AddBookScreen
import com.example.digitalbookmark.ui.screens.booklist.BookListScreen
import com.example.digitalbookmark.ui.screens.editbook.EditBookScreen
import com.example.digitalbookmark.ui.screens.settings.SettingsScreen
import com.example.digitalbookmark.ui.viewmodel.SettingsViewModel
import com.example.digitalbookmark.viewmodel.BookViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    settingsVm: SettingsViewModel
) {
    val viewModel: BookViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "list"
    ) {
        composable("list") {
            BookListScreen(navController, viewModel)
        }

        composable("add") {
            AddBookScreen(navController, viewModel)
        }

        composable("edit/{bookId}") { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId")?.toInt() ?: 0
            EditBookScreen(navController, viewModel, bookId)
        }

        composable("settings") {
            SettingsScreen(vm = settingsVm, onBack = { navController.popBackStack() })
        }
    }
}