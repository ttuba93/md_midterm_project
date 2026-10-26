package com.example.myshelf.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myshelf.ui.BookViewModel
import com.example.myshelf.ui.screens.DetailsScreen
import com.example.myshelf.ui.screens.EditBookScreen
import com.example.myshelf.ui.screens.LibraryScreen
import com.example.myshelf.ui.screens.StatsScreen

object Routes {
    const val LIBRARY = "library"
    const val STATS = "stats"
    const val DETAILS = "details/{bookId}"
    const val EDIT = "edit/{bookId}"

    fun details(id: Long) = "details/$id"
    fun edit(id: Long) = "edit/$id"
}

private data class BottomItem(val route: String, val label: String, val icon: ImageVector)

private val bottomItems = listOf(
    BottomItem(Routes.LIBRARY, "Library", Icons.Filled.LibraryBooks),
    BottomItem(Routes.STATS, "Stats", Icons.Filled.BarChart)
)

@Composable
fun MyShelfApp(viewModel: BookViewModel = viewModel()) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute == Routes.LIBRARY || currentRoute == Routes.STATS

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(Routes.LIBRARY) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.LIBRARY,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.LIBRARY) {
                val state by viewModel.libraryState.collectAsStateWithLifecycle()
                val query by viewModel.query.collectAsStateWithLifecycle()
                val picker by viewModel.picker.collectAsStateWithLifecycle()
                LibraryScreen(
                    state = state,
                    query = query,
                    picker = picker,
                    onQueryChange = viewModel::onQueryChange,
                    onStatusFilter = viewModel::onStatusFilter,
                    onGenreFilter = viewModel::onGenreFilter,
                    onBookClick = { navController.navigate(Routes.details(it)) },
                    onAddClick = { navController.navigate(Routes.edit(0L)) },
                    onPickBook = viewModel::pickNextBook,
                    onDismissPicker = viewModel::dismissPicker
                )
            }

            composable(Routes.STATS) {
                val state by viewModel.statsState.collectAsStateWithLifecycle()
                StatsScreen(state = state, onGoalChange = viewModel::setYearlyGoal)
            }

            composable(
                route = Routes.DETAILS,
                arguments = listOf(navArgument("bookId") { type = NavType.LongType })
            ) { entry ->
                val id = entry.arguments?.getLong("bookId") ?: 0L
                val books by viewModel.books.collectAsStateWithLifecycle()
                DetailsScreen(
                    book = books.find { it.id == id },
                    onBack = { navController.popBackStack() },
                    onEdit = { navController.navigate(Routes.edit(id)) },
                    onDelete = {
                        viewModel.deleteBook(id)
                        navController.popBackStack()
                    },
                    onRate = { viewModel.setRating(id, it) },
                    onStatusChange = { viewModel.changeStatus(id, it) }
                )
            }

            composable(
                route = Routes.EDIT,
                arguments = listOf(navArgument("bookId") { type = NavType.LongType })
            ) { entry ->
                val id = entry.arguments?.getLong("bookId") ?: 0L
                EditBookScreen(
                    existing = viewModel.getBook(id),
                    onSave = {
                        viewModel.saveBook(it)
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
