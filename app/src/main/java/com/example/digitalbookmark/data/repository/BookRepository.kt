package com.example.digitalbookmark.data.repository

import com.example.digitalbookmark.data.local.BookDao
import com.example.digitalbookmark.data.local.BookEntity
import kotlinx.coroutines.flow.Flow


class BookRepository(private val dao: BookDao) {

    val allBooks: Flow<List<BookEntity>> = dao.getAllBooks()

    suspend fun addBook(book: BookEntity) {
        dao.insertBook(book)
    }

    suspend fun deleteBook(book: BookEntity) {
        dao.deleteBook(book)
    }

    fun getBookById(id: Int): BookEntity {
        return dao.getBookById(id)
    }

    suspend fun updateBook(book: BookEntity) {
        dao.updateBook(book)
    }
}
