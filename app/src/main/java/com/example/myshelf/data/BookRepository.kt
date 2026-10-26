package com.example.myshelf.data

import java.time.Year
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BookRepository {

    private var nextId = 100L

    private val _books = MutableStateFlow(sampleBooks())
    val books: StateFlow<List<Book>> = _books.asStateFlow()

    private val _yearlyGoal = MutableStateFlow(12)
    val yearlyGoal: StateFlow<Int> = _yearlyGoal.asStateFlow()

    fun getBook(id: Long): Book? = _books.value.find { it.id == id }

    fun save(book: Book) {
        if (book.id == 0L) {
            val newBook = book.copy(id = nextId++)
            _books.update { it + newBook }
        } else {
            _books.update { list -> list.map { if (it.id == book.id) book else it } }
        }
    }

    fun delete(id: Long) {
        _books.update { list -> list.filterNot { it.id == id } }
    }

    fun setYearlyGoal(goal: Int) {
        _yearlyGoal.value = goal
    }

    private fun sampleBooks(): List<Book> {
        val year = Year.now().value
        val rowling = "J.K. Rowling"
        return listOf(
            Book(1, "Harry Potter and the Philosopher's Stone", rowling, Genre.FANTASY, BookFormat.PAPER, BookStatus.READ, 5, "", year),
            Book(2, "Harry Potter and the Chamber of Secrets", rowling, Genre.FANTASY, BookFormat.PAPER, BookStatus.READ, 5, "", year),
            Book(3, "Harry Potter and the Prisoner of Azkaban", rowling, Genre.FANTASY, BookFormat.PAPER, BookStatus.READ, 5, "", year),
            Book(4, "Harry Potter and the Goblet of Fire", rowling, Genre.FANTASY, BookFormat.PAPER, BookStatus.READ, 4, "", year),
            Book(5, "Harry Potter and the Order of the Phoenix", rowling, Genre.FANTASY, BookFormat.PAPER, BookStatus.READING),
            Book(6, "Harry Potter and the Half-Blood Prince", rowling, Genre.FANTASY, BookFormat.PAPER, BookStatus.PLANNED),
            Book(7, "Harry Potter and the Deathly Hallows", rowling, Genre.FANTASY, BookFormat.PAPER, BookStatus.PLANNED),
            Book(8, "Animal Farm", "George Orwell", Genre.CLASSIC, BookFormat.EBOOK, BookStatus.READ, 4, "", year),
            Book(9, "Iron Widow", "Xiran Jay Zhao", Genre.SCI_FI, BookFormat.EBOOK, BookStatus.READING)
        )
    }
}
