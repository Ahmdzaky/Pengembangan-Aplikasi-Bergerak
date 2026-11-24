# CoroutineDemo 🚀

Aplikasi Android berbasis **Kotlin** dan **Jetpack Compose** yang dirancang untuk mendemonstrasikan konsep dasar hingga lanjutan dari **Kotlin Coroutines** dan **Asynchronous Programming** sesuai dengan spesifikasi **Modul M17 (Latihan 9)**.

---

## 📌 Tujuan Aplikasi
Aplikasi ini bertujuan untuk memberikan gambaran praktis dan pemahaman mendalam tentang:
1. Pemrosesan data secara asinkron (*asynchronous processing*) tanpa memblokir antarmuka pengguna (*Main Thread/UI Thread*).
2. Eksekusi tugas secara paralel menggunakan *Structured Concurrency* (`coroutineScope`, `async`, dan `await`).
3. Pengalihan konteks eksekusi coroutine (*Dispatcher switching*) antara `Dispatchers.IO`, `Dispatchers.Default`, dan `Dispatchers.Main`.
4. Penanganan pembatalan (*Cancellation*) dan kegagalan (*Exception handling*) pada coroutine.
5. Pembuatan antarmuka pengguna yang adaptif dan responsif untuk berbagai ukuran layar (*Smartphone* & *Tablet*).

---

## 🛠️ Teknologi & Fitur Utama
* **Bahasa Pemrograman:** Kotlin
* **UI Toolkit:** Jetpack Compose (Material 3)
* **Arsitektur:** MVVM (Model-View-ViewModel)
* **Asynchronous & Concurrency:** 
  * Kotlin Coroutines (`viewModelScope`, `coroutineScope`, `launch`, `async`, `await`, `withContext`)
  * Kotlin Flow (`StateFlow`, `MutableStateFlow`, `asStateFlow`, `update`)
* **State & Lifecycle Handling:** `collectAsStateWithLifecycle()` dari `androidx.lifecycle.compose`
* **Desain Adaptif:** `BoxWithConstraints` untuk tata letak HP (vertikal) dan Tablet (dua kolom)

---

## ✨ Fitur Aplikasi
1. **Load Data Asinkron & Paralel:**
   Memuat daftar tugas (*Task List*), pengumuman (*Announcement*), dan jumlah mata kuliah (*Course Count*) secara bersamaan dari `TaskRepository` menggunakan 3 pemanggilan `async` yang berjalan secara paralel.
2. **Simulasi Error & Tombol Retry:**
   Menyediakan tombol *"Load (fail)"* untuk mensimulasikan kegagalan jaringan/server dan menampilkan pesan kesalahan serta tombol **Retry** untuk mencoba memuat data kembali.
3. **Pembatalan Coroutine (Cancel):**
   Tombol *"Cancel"* untuk membatalkan `Job` pemuatan data yang sedang berlangsung secara aman.
4. **Tap Counter (Non-blocking UI Test):**
   Tombol *"Tap me"* untuk membuktikan bahwa UI tetap responsif dan interaktif saat pemprosesan data di latar belakang sedang berjalan.
5. **Simulasi Freeze UI (Main Thread Blocking):**
   Tombol *"Simulate UI Freeze (3s)"* yang memanggil `Thread.sleep(3000L)` pada Main Thread untuk mendemonstrasikan bahaya pemblokiran UI thread vs efisiensi Coroutine.
6. **Pengurutan Data (Sorting):**
   Tombol *"Sort"* untuk mengurutkan daftar tugas berdasarkan judul secara komputasional menggunakan `Dispatchers.Default`.
7. **Responsive Layout:**
   Tampilan layar yang menyesuaikan secara otomatis antara smartphone (1 kolom) dan tablet (2 kolom: Action Panel & Info Panel di kiri, Task List di kanan).

---

## 🧠 Detail Implementasi Kotlin Coroutines

### 1. Structured Concurrency & Parallel Execution (`loadData`)
```kotlin
viewModelScope.launch {
    _uiState.update { it.copy(isLoading = true, errorMessage = null) }
    try {
        coroutineScope {
            val tasks = async { repository.getTasks(shouldFail) }
            val announcement = async { repository.getAnnouncement() }
            val courseCount = async { repository.getCourseCount() }

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
        // Exception handling & cancellation logic
    }
}
```

### 2. Dispatcher Allocation pada Repository
* **`Dispatchers.IO`**: Digunakan pada fungsi repository (`getTasks`, `getAnnouncement`, `getCourseCount`) untuk menyimulasikan operasi jaringan/I/O dengan `delay()`.
* **`Dispatchers.Default`**: Digunakan pada fungsi `sortTasks` untuk mengalokasikan tugas komputasi berat (pengurutan list) ke thread pool komputasi CPU.

---

## 📋 Daftar Pengerjaan M17 (Latihan 9)

| Nomor | Deskripsi Pengerjaan | Status |
|---|---|---|
| **Nomor 1** | Penambahan fungsi `getCourseCount()` dengan `delay(1500L)` di `TaskRepository`, penambahan panggilan `async` ketiga pada `loadData()` di `TasksViewModel`, serta penyimpanan hasilnya pada field `courseCount` di `TasksUiState`. |  Selesai |
| **Nomor 2** | Penanganan kondisi gagal (*Load with error*) dan implementasi tombol **Retry** pada UI `InfoPanel` untuk memicu pemuatan ulang data. |  Selesai |
| **Nomor 3** | Penanganan pesan kesalahan saat kegagalan pemuatan data maupun pembatalan job coroutine. |  Selesai |
| **Nomor 4** | Implementasi tombol pembatalan (*Cancel*) untuk menghentikan `Job` coroutine yang sedang berjalan. |  Selesai |
| **Nomor 5** | Pembuatan eksperimen `blockMainThread()` menggunakan `Thread.sleep(3000L)` untuk membandingkan pemblokiran Main Thread dengan keunggulan Coroutine. |  Selesai |
| **Nomor 6** | Implementasi tombol **Sort** yang memanfaatkan `Dispatchers.Default` untuk mengurutkan daftar tugas berdasarkan judul. |  Selesai |
| **Nomor 7** | Implementasi tata letak responsif (*Responsive UI*) pada `TasksScreen` yang membedakan tampilan Smartphone dan Tablet (`maxWidth >= 600.dp`). |  Selesai |

---

## 🚀 Cara Menjalankan Proyek

1. **Prasyarat:**
   * Android Studio (Koala, Ladybug, 2024.x / 2026.x atau versi lebih baru)
   * JDK 11 atau lebih baru
   * Android SDK (API Level 24 / Android 7.0 ke atas)

2. **Langkah-Langkah:**
   1. Clone repository ini:
      ```bash
      git clone https://github.com/<username_anda>/CoroutineDemo.git
      ```
   2. Buka folder proyek di Android Studio.
   3. Biarkan Gradle melakukan sinkronisasi (*Gradle Sync*).
   4. Jalankan aplikasi (*Run 'app'*) ke Emulator Android atau perangkat fisik Android yang terhubung.

---

## 🔍 Hasil Pemeriksaan Keamanan & Git
*  **Sensitive Data Check:** Tidak ditemukan API Key, password, token, credential, atau informasi rahasia pada seluruh source code dan konfigurasi Gradle.
*  **Gitignore Verification:** `local.properties`, folder `build/`, `.gradle/`, `.idea/caches/`, `.idea/workspace.xml`, dan file temporer IDE sudah dikecualikan dari pelacakan Git secara aman.
