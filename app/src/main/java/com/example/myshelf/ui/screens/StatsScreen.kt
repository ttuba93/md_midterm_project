package com.example.myshelf.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.myshelf.ui.StatsUiState
import com.example.myshelf.ui.components.EmptyState
import com.example.myshelf.ui.components.RatingBar
import com.example.myshelf.ui.components.SectionTitle
import com.example.myshelf.ui.components.ShelfCard
import com.example.myshelf.ui.components.ShelfTopBar
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun StatsScreen(state: StatsUiState, onGoalChange: (Int) -> Unit) {
    var showGoalDialog by rememberSaveable { mutableStateOf(false) }
    var goalText by rememberSaveable { mutableStateOf("") }

    Scaffold(topBar = { ShelfTopBar("Reading stats") }) { padding ->
        if (state.total == 0) {
            EmptyState("Add some books to see your statistics.", Modifier.padding(padding))
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    NumberCard("Read", state.read, Modifier.weight(1f))
                    NumberCard("Reading", state.reading, Modifier.weight(1f))
                    NumberCard("To read", state.planned, Modifier.weight(1f))
                }

                ShelfCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SectionTitle("Reading goal for this year")
                            TextButton(onClick = {
                                goalText = state.yearlyGoal.toString()
                                showGoalDialog = true
                            }) { Text("Change") }
                        }
                        LinearProgressIndicator(
                            progress = {
                                (state.readThisYear.toFloat() / state.yearlyGoal).coerceIn(0f, 1f)
                            },
                            modifier = Modifier.fillMaxWidth().height(10.dp)
                        )
                        Text(
                            if (state.readThisYear >= state.yearlyGoal) "Goal reached! ${state.readThisYear} of ${state.yearlyGoal} books"
                            else "${state.readThisYear} of ${state.yearlyGoal} books"
                        )
                    }
                }

                ShelfCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SectionTitle("Average rating")
                        if (state.averageRating == 0.0) {
                            Text("No rated books yet")
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RatingBar(rating = state.averageRating.roundToInt())
                                Text(
                                    String.format(Locale.US, "  %.1f / 5", state.averageRating),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }

                ShelfCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SectionTitle("Paper vs e-book vs audio")
                        val maxCount = state.formatCounts.values.maxOrNull() ?: 0
                        state.formatCounts.forEach { (format, count) ->
                            BarRow(format.label, count, maxCount)
                        }
                    }
                }

                ShelfCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SectionTitle("Favourite genres")
                        val maxCount = state.genreCounts.values.maxOrNull() ?: 0
                        state.genreCounts.forEach { (genre, count) ->
                            BarRow(genre.label, count, maxCount)
                        }
                    }
                }
            }
        }
    }

    if (showGoalDialog) {
        AlertDialog(
            onDismissRequest = { showGoalDialog = false },
            title = { Text("Books to read this year") },
            text = {
                OutlinedTextField(
                    value = goalText,
                    onValueChange = { goalText = it.filter(Char::isDigit).take(3) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    goalText.toIntOrNull()?.takeIf { it > 0 }?.let(onGoalChange)
                    showGoalDialog = false
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showGoalDialog = false }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun NumberCard(label: String, value: Int, modifier: Modifier = Modifier) {
    ShelfCard(modifier) {
        Column(
            Modifier.fillMaxWidth().padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value.toString(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun BarRow(label: String, count: Int, max: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, modifier = Modifier.width(120.dp), style = MaterialTheme.typography.bodyMedium)
        LinearProgressIndicator(
            progress = { if (max == 0) 0f else count.toFloat() / max },
            modifier = Modifier.weight(1f).height(10.dp)
        )
        Text("  $count", style = MaterialTheme.typography.bodyMedium)
    }
}
