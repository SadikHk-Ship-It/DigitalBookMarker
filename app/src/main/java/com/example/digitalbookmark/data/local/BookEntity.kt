package com.example.digitalbookmark.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val pageNumber: Int,
    val review: String,
    val rating: Int,
    val imageUri: String,
    val status: String
)
