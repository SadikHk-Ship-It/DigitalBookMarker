package com.example.digitalbookmark.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.digitalbookmark.data.local.BookDatabase
import com.example.digitalbookmark.data.local.BookEntity
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BookViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = BookDatabase.getDatabase(application).bookDao()

    val books: StateFlow<List<BookEntity>> =
        dao.getAllBooks()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun getBookById(id: Int): BookEntity? {
        return books.value.firstOrNull { it.id == id }
    }

    fun addBook(book: BookEntity) {
        viewModelScope.launch { dao.insertBook(book) }
    }

    fun updateBook(book: BookEntity) {
        viewModelScope.launch { dao.updateBook(book) }
    }

    fun deleteBook(book: BookEntity) {
        viewModelScope.launch { dao.deleteBook(book) }
    }
}
