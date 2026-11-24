package com.example.coroutinedemo.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun TasksScreen(
    modifier: Modifier = Modifier,
    viewModel: TasksViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    TasksContent(
        state = state,
        onLoad = { viewModel.loadData() },
        onLoadWithError = {
            viewModel.loadData(shouldFail = true)
        },
        onCancel = viewModel::cancelLoading,
        onTap = viewModel::onTap,
        onBlock = viewModel::blockMainThread,
        onSort = viewModel::sortTasks,
        onRetry = { viewModel.loadData() },
        modifier = modifier
    )
}

@Composable
fun TasksContent(
    state: TasksUiState,
    onLoad: () -> Unit,
    onLoadWithError: () -> Unit,
    onCancel: () -> Unit,
    onTap: () -> Unit,
    onBlock: () -> Unit,
    onSort: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {

        val isTablet = maxWidth >= 600.dp

        if (isTablet) {

            // TABLET LAYOUT
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                Column(
                    modifier = Modifier
                        .width(280.dp)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Text(
                        "Coroutine Demo",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    ActionPanel(
                        state = state,
                        onLoad = onLoad,
                        onLoadWithError = onLoadWithError,
                        onCancel = onCancel,
                        onTap = onTap,
                        onBlock = onBlock,
                        isTablet = true
                    )

                    InfoPanel(
                        state = state,
                        onRetry = onRetry
                    )
                }

                TaskList(
                    state = state,
                    onSort = onSort,
                    modifier = Modifier.weight(1f)
                )
            }

        } else {

            // PHONE LAYOUT
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    "Coroutine Demo",
                    style = MaterialTheme.typography.headlineSmall
                )

                ActionPanel(
                    state = state,
                    onLoad = onLoad,
                    onLoadWithError = onLoadWithError,
                    onCancel = onCancel,
                    onTap = onTap,
                    onBlock = onBlock,
                    isTablet = false
                )

                InfoPanel(
                    state = state,
                    onRetry = onRetry
                )

                TaskList(
                    state = state,
                    onSort = onSort,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ActionPanel(
    state: TasksUiState,
    onLoad: () -> Unit,
    onLoadWithError: () -> Unit,
    onCancel: () -> Unit,
    onTap: () -> Unit,
    onBlock: () -> Unit,
    isTablet: Boolean
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        if (isTablet) {

            Button(
                onClick = onLoad,
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Load")
            }

            OutlinedButton(
                onClick = onLoadWithError,
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Load (fail)")
            }

            OutlinedButton(
                onClick = onCancel,
                enabled = state.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel")
            }

            Button(
                onClick = onTap,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Tap me: ${state.tapCount}")
            }

            OutlinedButton(
                onClick = onBlock,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Simulate UI Freeze (3s)")
            }

        } else {

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {

                Button(
                    onClick = onLoad,
                    enabled = !state.isLoading,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Load")
                }

                OutlinedButton(
                    onClick = onLoadWithError,
                    enabled = !state.isLoading,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Fail")
                }

                OutlinedButton(
                    onClick = onCancel,
                    enabled = state.isLoading,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Button(
                    onClick = onTap,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Tap me: ${state.tapCount}")
                }

                OutlinedButton(
                    onClick = onBlock,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Block UI")
                }
            }
        }
    }
}

@Composable
fun InfoPanel(
    state: TasksUiState,
    onRetry: () -> Unit
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        if (state.isLoading) {

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CircularProgressIndicator()

                Text("Loading data...")
            }
        }

        // LATIHAN 9 NOMOR 2
        // Retry muncul jika errorMessage tidak null
        state.errorMessage?.let { message ->

            Text(
                message,
                color = MaterialTheme.colorScheme.error
            )

            Button(onClick = onRetry) {
                Text("Retry")
            }
        }

        if (state.announcement.isNotEmpty()) {

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        "Announcement",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(state.announcement)
                }
            }
        }

        if (state.courseCount > 0) {

            Text(
                "Course Count: ${state.courseCount}",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun TaskList(
    state: TasksUiState,
    onSort: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                "Task List",
                style = MaterialTheme.typography.titleLarge
            )

            OutlinedButton(onClick = onSort) {
                Text("Sort")
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            items(
                state.tasks,
                key = { it.id }
            ) { task ->

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {

                        Text(
                            task.title,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            task.course,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}
