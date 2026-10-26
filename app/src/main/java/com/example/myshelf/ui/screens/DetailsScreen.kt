package com.example.myshelf.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myshelf.data.Book
import com.example.myshelf.data.BookStatus
import com.example.myshelf.ui.components.BookChips
import com.example.myshelf.ui.components.EmptyState
import com.example.myshelf.ui.components.RatingBar
import com.example.myshelf.ui.components.SectionTitle
import com.example.myshelf.ui.components.ShelfCard
import com.example.myshelf.ui.components.ShelfTopBar

@Composable
fun DetailsScreen(
    book: Book?,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onRate: (Int) -> Unit,
    onStatusChange: (BookStatus) -> Unit
) {
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ShelfTopBar(
                title = "Book details",
                onBack = onBack,
                actions = {
                    if (book != null) {
                        IconButton(onClick = onEdit) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (book == null) {
            EmptyState("Book not found", Modifier.padding(padding))
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Text(
                        book.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "by ${book.author}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                BookChips(book)

                when (book.status) {
                    BookStatus.READ -> ShelfCard(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            SectionTitle("Your rating")
                            Spacer(Modifier.height(8.dp))
                            RatingBar(rating = book.rating, onRatingChange = onRate)
                            book.finishedYear?.let {
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "Finished in $it",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    BookStatus.READING -> Button(
                        onClick = { onStatusChange(BookStatus.READ) },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Mark as finished") }
                    BookStatus.PLANNED -> Button(
                        onClick = { onStatusChange(BookStatus.READING) },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Start reading") }
                }

                if (book.quote.isNotBlank()) {
                    ShelfCard(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            SectionTitle("Favourite quote / note")
                            Spacer(Modifier.height(8.dp))
                            Text(
                                book.quote,
                                style = MaterialTheme.typography.bodyLarge,
                                fontStyle = FontStyle.Italic
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog && book != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete book?") },
            text = { Text("\"${book.title}\" will be removed from your shelf.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onDelete()
                }) { Text("Delete") }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") } }
        )
    }
}
