package com.example.coroutinedemo.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coroutinedemo.data.TaskRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TasksViewModel(
    private val repository: TaskRepository = TaskRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(TasksUiState())

    val uiState: StateFlow<TasksUiState> =
        _uiState.asStateFlow()

    private var loadJob: Job? = null

    // Memuat seluruh data secara bersamaan
    fun loadData(shouldFail: Boolean = false) {

        loadJob?.cancel()

        loadJob = viewModelScope.launch {

            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            try {

                coroutineScope {

                    // Permintaan pertama
                    val tasks = async {
                        repository.getTasks(shouldFail)
                    }

                    // Permintaan kedua
                    val announcement = async {
                        repository.getAnnouncement()
                    }

                    // LATIHAN 9 NOMOR 1
                    // Permintaan ketiga
                    val courseCount = async {
                        repository.getCourseCount()
                    }

                    val loadedTasks = tasks.await()
                    val loadedAnnouncement = announcement.await()
                    val loadedCourseCount = courseCount.await()

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            tasks = loadedTasks,
                            announcement = loadedAnnouncement,
                            courseCount = loadedCourseCount
                        )
                    }
                }

            } catch (e: Exception) {

                // LATIHAN 9 NOMOR 3
                // CancellationException juga tertangkap
                val message = if (e is CancellationException) {
                    "Error: ${e::class.simpleName} - " +
                            (e.message ?: "Job cancelled")
                } else {
                    "Failed to load: ${e.message ?: "Unknown error"}"
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = message
                    )
                }
            }
        }
    }

    // Membatalkan proses loading
    fun cancelLoading() {
        loadJob?.cancel()
    }

    // Menghitung tombol Tap me
    fun onTap() {
        _uiState.update {
            it.copy(
                tapCount = it.tapCount + 1
            )
        }
    }

    // Demonstrasi main thread terhambat
    fun blockMainThread() {
        Thread.sleep(3000L)
    }

    // LATIHAN 9 NOMOR 6
    // Mengurutkan daftar tugas
    fun sortTasks() {

        viewModelScope.launch {

            val sortedTasks = repository.sortTasks(
                _uiState.value.tasks
            )

            _uiState.update {
                it.copy(
                    tasks = sortedTasks
                )
            }
        }
    }
}
