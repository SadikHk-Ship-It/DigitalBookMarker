package com.example.digitalbookmark.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class BookType(val label: String, val usesTimestamp: Boolean = false) {
    BOOK("Book"),
    LIGHT_NOVEL("Light Novel"),
    COMIC("Comic"),
    MANGA("Manga"),
    MANHWA("Manhwa"),
    AUDIOBOOK("Audiobook", usesTimestamp = true),
    OTHER("Other")
}

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String = "",
    val pageNumber: Int = 0,
    val review: String = "",
    val rating: Int = 0,
    val imageUri: String = "",
    val status: String = "Reading",
    val type: String = BookType.BOOK.name,
    val timestampSeconds: Long = 0L      // only used for audiobooks
) {
    val bookType: BookType
        get() = runCatching { BookType.valueOf(type) }.getOrDefault(BookType.OTHER)
}

fun formatTimestamp(totalSeconds: Long): String {
    val h = totalSeconds / 3600
    val m = (totalSeconds % 3600) / 60
    val s = totalSeconds % 60
    return "%d:%02d:%02d".format(h, m, s)
}

fun parseTimestamp(h: String, m: String, s: String): Long =
    (h.toLongOrNull() ?: 0L) * 3600 + (m.toLongOrNull() ?: 0L) * 60 + (s.toLongOrNull() ?: 0L)