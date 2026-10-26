package com.example.myshelf.data

enum class BookStatus(val label: String) {
    READ("Read"),
    READING("Reading"),
    PLANNED("To read")
}

enum class BookFormat(val label: String) {
    PAPER("Paper"),
    EBOOK("E-book"),
    AUDIO("Audio")
}

enum class Genre(val label: String) {
    FANTASY("Fantasy"),
    SCI_FI("Sci-Fi"),
    MYSTERY("Mystery"),
    ROMANCE("Romance"),
    CLASSIC("Classic"),
    NONFICTION("Non-fiction"),
    SELF_DEV("Self-development"),
    TECH("Technology"),
    HISTORY("History"),
    OTHER("Other")
}

data class Book(
    val id: Long = 0L,
    val title: String,
    val author: String,
    val genre: Genre = Genre.OTHER,
    val format: BookFormat = BookFormat.PAPER,
    val status: BookStatus = BookStatus.PLANNED,
    val rating: Int = 0,
    val quote: String = "",
    val finishedYear: Int? = null
)
