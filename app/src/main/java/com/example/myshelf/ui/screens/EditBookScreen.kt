package com.example.myshelf.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myshelf.data.Book
import com.example.myshelf.data.BookFormat
import com.example.myshelf.data.BookStatus
import com.example.myshelf.data.Genre
import com.example.myshelf.ui.components.ChipSelector
import com.example.myshelf.ui.components.OptionDropdown
import com.example.myshelf.ui.components.RatingBar
import com.example.myshelf.ui.components.SectionTitle
import com.example.myshelf.ui.components.ShelfTopBar

@Composable
fun EditBookScreen(
    existing: Book?,
    onSave: (Book) -> Unit,
    onBack: () -> Unit
) {
    val key = existing?.id ?: 0L

    var title by rememberSaveable(key) { mutableStateOf(existing?.title ?: "") }
    var author by rememberSaveable(key) { mutableStateOf(existing?.author ?: "") }
    var genre by rememberSaveable(key) { mutableStateOf(existing?.genre ?: Genre.OTHER) }
    var format by rememberSaveable(key) { mutableStateOf(existing?.format ?: BookFormat.PAPER) }
    var status by rememberSaveable(key) { mutableStateOf(existing?.status ?: BookStatus.PLANNED) }
    var rating by rememberSaveable(key) { mutableStateOf(existing?.rating ?: 0) }
    var quote by rememberSaveable(key) { mutableStateOf(existing?.quote ?: "") }
    var showError by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ShelfTopBar(
                title = if (existing == null) "Add book" else "Edit book",
                onBack = onBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Title *") },
                isError = showError && title.isBlank(),
                supportingText = { if (showError && title.isBlank()) Text("Title is required") },
                singleLine = true
            )
            OutlinedTextField(
                value = author,
                onValueChange = { author = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Author") },
                singleLine = true
            )

            SectionTitle("Genre")
            OptionDropdown(
                options = Genre.entries,
                selected = genre,
                optionLabel = { it.label },
                onSelected = { genre = it }
            )

            SectionTitle("Format")
            ChipSelector(
                options = BookFormat.entries,
                selected = format,
                optionLabel = { it.label },
                onSelected = { format = it }
            )

            SectionTitle("Status")
            ChipSelector(
                options = BookStatus.entries,
                selected = status,
                optionLabel = { it.label },
                onSelected = { status = it }
            )

            if (status == BookStatus.READ) {
                SectionTitle("Your rating")
                RatingBar(rating = rating, onRatingChange = { rating = it })
            }

            OutlinedTextField(
                value = quote,
                onValueChange = { quote = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Favourite quote or note") },
                minLines = 3
            )

            Button(
                onClick = {
                    if (title.isBlank()) {
                        showError = true
                    } else {
                        onSave(
                            Book(
                                id = existing?.id ?: 0L,
                                title = title.trim(),
                                author = author.trim().ifBlank { "Unknown author" },
                                genre = genre,
                                format = format,
                                status = status,
                                rating = if (status == BookStatus.READ) rating else 0,
                                quote = quote.trim(),
                                finishedYear = existing?.finishedYear
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (existing == null) "Add to shelf" else "Save changes") }
        }
    }
}
