package com.example.myshelf.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myshelf.data.Book
import com.example.myshelf.data.BookStatus
import com.example.myshelf.data.Genre
import com.example.myshelf.ui.LibraryUiState
import com.example.myshelf.ui.PickerState
import com.example.myshelf.ui.components.BookChips
import com.example.myshelf.ui.components.EmptyState
import com.example.myshelf.ui.components.RatingBar
import com.example.myshelf.ui.components.ShelfCard
import com.example.myshelf.ui.components.ShelfTopBar

@Composable
fun LibraryScreen(
    state: LibraryUiState,
    query: String,
    picker: PickerState,
    onQueryChange: (String) -> Unit,
    onStatusFilter: (BookStatus?) -> Unit,
    onGenreFilter: (Genre?) -> Unit,
    onBookClick: (Long) -> Unit,
    onAddClick: () -> Unit,
    onPickBook: () -> Unit,
    onDismissPicker: () -> Unit
) {
    Scaffold(
        topBar = {
            ShelfTopBar(
                title = "My Shelf",
                actions = {
                    IconButton(onClick = onPickBook) {
                        Icon(Icons.Filled.Casino, contentDescription = "What should I read next?")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Filled.Add, contentDescription = "Add book")
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(horizontal = 16.dp)) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search by title or author") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(Icons.Filled.Close, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true
            )

            LazyRow(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = state.statusFilter == null,
                        onClick = { onStatusFilter(null) },
                        label = { Text("All") }
                    )
                }
                items(BookStatus.entries) { status ->
                    FilterChip(
                        selected = state.statusFilter == status,
                        onClick = { onStatusFilter(if (state.statusFilter == status) null else status) },
                        label = { Text(status.label) }
                    )
                }
            }

            LazyRow(
                modifier = Modifier.padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = state.genreFilter == null,
                        onClick = { onGenreFilter(null) },
                        label = { Text("All genres") }
                    )
                }
                items(Genre.entries) { genre ->
                    FilterChip(
                        selected = state.genreFilter == genre,
                        onClick = { onGenreFilter(if (state.genreFilter == genre) null else genre) },
                        label = { Text(genre.label) }
                    )
                }
            }

            if (state.books.isEmpty()) {
                EmptyState(
                    if (state.totalCount == 0) "Your shelf is empty.\nTap + to add your first book."
                    else "No books match your search or filters."
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(top = 12.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.books, key = { it.id }) { book ->
                        BookCard(book = book, onClick = { onBookClick(book.id) })
                    }
                }
            }
        }
    }

    if (picker.visible) {
        PickerDialog(
            state = picker,
            onDismiss = onDismissPicker,
            onOpen = { book ->
                onDismissPicker()
                onBookClick(book.id)
            }
        )
    }
}

@Composable
private fun BookCard(book: Book, onClick: () -> Unit, modifier: Modifier = Modifier) {
    ShelfCard(modifier = modifier.fillMaxWidth(), onClick = onClick) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(book.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(
                book.author,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            BookChips(book, Modifier.padding(top = 4.dp))
            if (book.status == BookStatus.READ && book.rating > 0) {
                RatingBar(rating = book.rating, starSize = 18.dp, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}

@Composable
private fun PickerDialog(state: PickerState, onDismiss: () -> Unit, onOpen: (Book) -> Unit) {
    val book = state.book
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("What should I read next?") },
        text = {
            when {
                state.isPicking -> Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 3.dp)
                    Spacer(Modifier.width(12.dp))
                    Text("Choosing a book for you…")
                }
                book == null -> Text(
                    "Your \"To read\" list is empty" +
                        (state.genreLabel?.let { " for genre $it" } ?: "") +
                        ". Add some books first!"
                )
                else -> Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(book.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("by ${book.author}")
                    Text(
                        "${book.genre.label} • ${book.format.label}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            if (!state.isPicking && book != null) {
                TextButton(onClick = { onOpen(book) }) { Text("Open") }
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Close") } }
    )
}
