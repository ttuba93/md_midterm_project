package com.example.myshelf.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myshelf.data.Book
import com.example.myshelf.data.BookFormat
import com.example.myshelf.data.BookRepository
import com.example.myshelf.data.BookStatus
import com.example.myshelf.data.Genre
import java.time.Year
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class LibraryUiState(
    val books: List<Book> = emptyList(),
    val totalCount: Int = 0,
    val statusFilter: BookStatus? = null,
    val genreFilter: Genre? = null
)

data class StatsUiState(
    val total: Int = 0,
    val read: Int = 0,
    val reading: Int = 0,
    val planned: Int = 0,
    val averageRating: Double = 0.0,
    val formatCounts: Map<BookFormat, Int> = emptyMap(),
    val genreCounts: Map<Genre, Int> = emptyMap(),
    val yearlyGoal: Int = 12,
    val readThisYear: Int = 0
)

data class PickerState(
    val visible: Boolean = false,
    val isPicking: Boolean = false,
    val book: Book? = null,
    val genreLabel: String? = null
)

class BookViewModel(
    private val repository: BookRepository = BookRepository()
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()
    private val statusFilter = MutableStateFlow<BookStatus?>(null)
    private val genreFilter = MutableStateFlow<Genre?>(null)

    val books: StateFlow<List<Book>> = repository.books

    val libraryState: StateFlow<LibraryUiState> = combine(
        repository.books, _query, statusFilter, genreFilter
    ) { books, query, status, genre ->
        LibraryUiState(
            books = books.filter { book ->
                (status == null || book.status == status) &&
                    (genre == null || book.genre == genre) &&
                    (query.isBlank() ||
                        book.title.contains(query, ignoreCase = true) ||
                        book.author.contains(query, ignoreCase = true))
            },
            totalCount = books.size,
            statusFilter = status,
            genreFilter = genre
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LibraryUiState())

    val statsState: StateFlow<StatsUiState> = combine(
        repository.books, repository.yearlyGoal
    ) { books, goal ->
        val readBooks = books.filter { it.status == BookStatus.READ }
        val rated = readBooks.filter { it.rating > 0 }
        StatsUiState(
            total = books.size,
            read = readBooks.size,
            reading = books.count { it.status == BookStatus.READING },
            planned = books.count { it.status == BookStatus.PLANNED },
            averageRating = if (rated.isEmpty()) 0.0 else rated.map { it.rating }.average(),
            formatCounts = BookFormat.entries.associateWith { f -> books.count { it.format == f } },
            genreCounts = books.groupingBy { it.genre }.eachCount()
                .entries.sortedByDescending { it.value }
                .associate { it.key to it.value },
            yearlyGoal = goal,
            readThisYear = readBooks.count { it.finishedYear == Year.now().value }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StatsUiState())

    private val _picker = MutableStateFlow(PickerState())
    val picker: StateFlow<PickerState> = _picker.asStateFlow()
    private var pickJob: Job? = null

    fun onQueryChange(value: String) { _query.value = value }
    fun onStatusFilter(status: BookStatus?) { statusFilter.value = status }
    fun onGenreFilter(genre: Genre?) { genreFilter.value = genre }

    fun getBook(id: Long): Book? = repository.getBook(id)

    fun saveBook(book: Book) {
        val prepared = if (book.status == BookStatus.READ) {
            book.copy(finishedYear = book.finishedYear ?: Year.now().value)
        } else {
            book.copy(rating = 0, finishedYear = null)
        }
        repository.save(prepared)
    }

    fun deleteBook(id: Long) = repository.delete(id)

    fun setRating(id: Long, rating: Int) {
        repository.getBook(id)?.let { repository.save(it.copy(rating = rating)) }
    }

    fun changeStatus(id: Long, status: BookStatus) {
        repository.getBook(id)?.let { saveBook(it.copy(status = status)) }
    }

    fun setYearlyGoal(goal: Int) = repository.setYearlyGoal(goal)

    fun pickNextBook() {
        pickJob?.cancel()
        pickJob = viewModelScope.launch {
            val genre = genreFilter.value
            _picker.value = PickerState(visible = true, isPicking = true, genreLabel = genre?.label)
            delay(900)
            val pool = repository.books.value.filter {
                it.status == BookStatus.PLANNED && (genre == null || it.genre == genre)
            }
            _picker.value = PickerState(
                visible = true,
                isPicking = false,
                book = pool.randomOrNull(),
                genreLabel = genre?.label
            )
        }
    }

    fun dismissPicker() {
        pickJob?.cancel()
        _picker.value = PickerState()
    }
}
