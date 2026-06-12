# 📰 NNews — Modern Android News Application

<p align="center">
  <em>Stay informed, stay ahead.</em>
</p>

<p align="center">
  <img alt="Platform" src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white"/>
  <img alt="Language" src="https://img.shields.io/badge/Language-Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/>
  <img alt="Min SDK" src="https://img.shields.io/badge/Min%20SDK-24-blue?style=for-the-badge"/>
  <img alt="Architecture" src="https://img.shields.io/badge/Architecture-MVVM-9C27B0?style=for-the-badge"/>
  <img alt="License" src="https://img.shields.io/badge/License-MIT-green?style=for-the-badge"/>
</p>

<p align="center">
  <img alt="Build" src="https://img.shields.io/badge/Build-Passing-success?style=flat-square"/>
  <img alt="API" src="https://img.shields.io/badge/API-GNews.io-00BCD4?style=flat-square"/>
  <img alt="Firebase" src="https://img.shields.io/badge/Firebase-Enabled-FFCA28?style=flat-square&logo=firebase&logoColor=black"/>
  <img alt="Material" src="https://img.shields.io/badge/Material-Design%203-757575?style=flat-square&logo=material-design&logoColor=white"/>
</p>

---

## 📖 Daftar Isi

- [Tentang Aplikasi](#-tentang-aplikasi)
- [Fitur Lengkap](#-fitur-lengkap)
- [Arsitektur](#-arsitektur)
- [Tech Stack](#-tech-stack)
- [Struktur Project](#-struktur-project)
- [Persyaratan Sistem](#-persyaratan-sistem)
- [Cara Setup & Instalasi](#-cara-setup--instalasi)
- [Konfigurasi API](#-konfigurasi-api)
- [Konfigurasi Firebase](#-konfigurasi-firebase)
- [Menjalankan Aplikasi](#-menjalankan-aplikasi)
- [Panduan Penggunaan](#-panduan-penggunaan)
- [Database & Cache](#-database--cache)
- [Dark Mode](#-dark-mode)
- [Offline Mode](#-offline-mode)
- [Troubleshooting](#-troubleshooting)
- [Changelog](#-changelog)
- [Informasi Developer](#-informasi-developer)

---

## 📱 Tentang Aplikasi

**NNews** adalah aplikasi portal berita Android native modern yang dirancang untuk memberikan pengalaman membaca berita yang menyenangkan, cepat, dan elegan. Dibangun dari nol menggunakan **Java** dengan menerapkan prinsip **Clean Architecture** dan **MVVM Pattern**, aplikasi ini merupakan proyek mandiri pengembangan mobile untuk keperluan akademis.

### 🎯 Tujuan Proyek

- Membangun aplikasi Android yang mengonsumsi REST API secara nyata
- Menerapkan Clean Architecture dalam proyek solo yang terstruktur
- Mengimplementasikan offline-first approach dengan Room Database
- Mempraktikkan Material Design 3 untuk UI yang modern dan konsisten

### 🌟 Keunggulan

- **Offline-First** — Berita tetap dapat dibaca meski tanpa koneksi internet
- **Realtime Search** — Pencarian dengan debounce untuk efisiensi API call
- **Dark Mode** — Dukungan tema gelap yang persisten antar sesi
- **Clean Code** — Tidak ada hardcoded value, menggunakan resource file secara konsisten
- **Scalable** — Arsitektur siap untuk pengembangan lebih lanjut

---

## ✨ Fitur Lengkap

### 🏠 Home Screen
- Menampilkan daftar berita terkini dari GNews API
- Greeting dinamis berdasarkan waktu (Pagi/Siang/Sore/Malam)
- Filter berita berdasarkan **8 kategori**: General, World, Technology, Business, Sports, Science, Health, Entertainment
- Shimmer loading animation saat data sedang dimuat
- Pull-to-refresh untuk memperbarui berita
- Banner offline saat menampilkan data dari cache

### 🔍 Search
- Pencarian berita secara realtime dengan **debounce 600ms**
- Kategori otomatis tersembunyi saat mode pencarian aktif
- Kategori kembali muncul saat pencarian dikosongkan
- Submit langsung saat menekan Enter/tombol search
- Retry otomatis saat pencarian gagal

### 📰 Detail Berita
- Hero image fullwidth dengan efek parallax
- Judul, sumber, dan tanggal terformat rapi
- Konten artikel lengkap (dibersihkan dari tag GNews)
- Tombol **Share** untuk berbagi artikel via aplikasi lain
- Tombol **Open in Browser** untuk membaca artikel penuh
- Tombol **Bookmark** untuk menyimpan artikel
- Indikator status bookmark (tersimpan/belum)

### 🔖 Bookmark
- Daftar artikel yang disimpan dari Room Database (lokal)
- **Swipe to delete** dengan konfirmasi dialog
- Hapus per artikel via ikon bookmark di card
- Navigasi ke detail artikel dari bookmark
- Empty state saat belum ada bookmark

### 🌙 Dark Mode
- Toggle dark/light mode dari halaman Settings
- Preferensi tersimpan via SharedPreferences
- Otomatis apply saat aplikasi dibuka kembali
- Warna dikustomisasi untuk kedua mode (bukan hanya auto system)

### ⚙️ Settings
- **Dark Mode Toggle** — Ganti tema dengan satu klik
- **Clear Cache** — Hapus HTTP cache + Glide image cache
- **Clear All Bookmarks** — Hapus semua bookmark dengan konfirmasi
- **App Version** — Menampilkan versi aplikasi dari BuildConfig
- **About Dialog** — Informasi aplikasi dan tagline

### 📡 Offline Mode
- Berita di-cache ke Room Database setiap kali berhasil dimuat
- Cache tersimpan per kategori secara terpisah
- Saat offline, data otomatis diambil dari cache
- Banner informatif ditampilkan saat melihat data cache

### 🔔 Error Handling
- Error state dengan icon, pesan, dan tombol Retry
- Pesan error spesifik: No Internet, Rate Limit, Invalid API Key, Server Error
- Fallback ke cache saat API gagal (online maupun offline)
- Empty state saat tidak ada artikel ditemukan

---

## 🏗️ Arsitektur

Aplikasi menggunakan **Clean Architecture** dengan **MVVM Pattern**:

```
┌─────────────────────────────────────────────────┐
│                   UI Layer                       │
│  Fragment → ViewModel → LiveData → Adapter       │
└─────────────────────┬───────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────┐
│               Domain Layer                       │
│         Repository (Single Source of Truth)      │
└──────────────┬──────────────┬───────────────────┘
               │              │
┌──────────────▼──┐    ┌──────▼──────────────────┐
│  Remote Layer   │    │     Local Layer          │
│  Retrofit API   │    │  Room Database           │
│  GNews API      │    │  Bookmark + Cache        │
└─────────────────┘    └─────────────────────────┘
```

### Prinsip yang Diterapkan

| Prinsip | Implementasi |
|---------|-------------|
| **Single Responsibility** | Setiap class punya 1 tanggung jawab |
| **DRY** | Tidak ada duplikasi kode, semua value di resource file |
| **Separation of Concerns** | UI, Business Logic, Data terpisah jelas |
| **Repository Pattern** | Satu pintu masuk untuk semua data |
| **Observer Pattern** | LiveData + MediatorLiveData untuk reaktivitas |
| **Singleton Pattern** | NetworkClient, NewsRepository, NewsDatabase |

---

## 🛠️ Tech Stack

### Core
| Library | Versi | Fungsi |
|---------|-------|--------|
| **Java** | 11 | Bahasa pemrograman utama |
| **Android SDK** | API 35 | Target platform |
| **Material Design 3** | 1.12.0 | Komponen UI modern |
| **ViewBinding** | - | Binding layout tanpa findViewById |

### Architecture Components
| Library | Versi | Fungsi |
|---------|-------|--------|
| **ViewModel** | 2.8.3 | Menyimpan UI state |
| **LiveData** | 2.8.3 | Observable data holder |
| **MediatorLiveData** | 2.8.3 | Combine multiple LiveData |
| **Room** | 2.6.1 | Local database (SQLite ORM) |

### Navigation
| Library | Versi | Fungsi |
|---------|-------|--------|
| **Navigation Component** | 2.7.7 | Single-activity navigation |
| **Safe Args** | 2.7.7 | Type-safe navigation arguments |

### Network
| Library | Versi | Fungsi |
|---------|-------|--------|
| **Retrofit** | 2.11.0 | HTTP client untuk REST API |
| **OkHttp** | 4.12.0 | HTTP engine + interceptor |
| **Gson** | 2.11.0 | JSON serialization/deserialization |

### UI & UX
| Library | Versi | Fungsi |
|---------|-------|--------|
| **Glide** | 4.16.0 | Image loading & caching |
| **Facebook Shimmer** | 0.5.0 | Skeleton loading animation |
| **SwipeRefreshLayout** | 1.1.0 | Pull-to-refresh |
| **SplashScreen API** | 1.0.1 | Modern splash screen |

### Firebase
| Library | Versi | Fungsi |
|---------|-------|--------|
| **Firebase BOM** | 33.1.0 | Bill of Materials |
| **Firebase Auth** | - | Autentikasi pengguna |
| **Firebase Analytics** | - | Analytics aplikasi |

---

## 📁 Struktur Project

```
NNews/
│
├── app/
│   ├── branding/
│   │   ├── logo_light_master.png
│   │   ├── logo_dark_master.png
│   │   └── ic_launcher_playstore.png
│   │
│   ├── src/main/
│   │   ├── java/com/example/nnews/
│   │   │   │
│   │   │   ├── data/
│   │   │   │   ├── local/
│   │   │   │   │   ├── ArticleTypeConverter.java
│   │   │   │   │   ├── CacheDao.java
│   │   │   │   │   ├── NewsDao.java
│   │   │   │   │   └── NewsDatabase.java
│   │   │   │   │
│   │   │   │   ├── model/
│   │   │   │   │   ├── Article.java
│   │   │   │   │   ├── CachedArticle.java
│   │   │   │   │   └── GNewsResponse.java
│   │   │   │   │
│   │   │   │   ├── remote/
│   │   │   │   │   ├── ApiKeyInterceptor.java
│   │   │   │   │   ├── NetworkClient.java
│   │   │   │   │   ├── NewsApiService.java
│   │   │   │   │   └── RemoteDataSource.java
│   │   │   │   │
│   │   │   │   └── repository/
│   │   │   │       └── NewsRepository.java
│   │   │   │
│   │   │   ├── ui/
│   │   │   │   ├── bookmark/
│   │   │   │   │   └── BookmarkFragment.java
│   │   │   │   ├── detail/
│   │   │   │   │   └── DetailFragment.java
│   │   │   │   ├── home/
│   │   │   │   │   └── HomeFragment.java
│   │   │   │   ├── settings/
│   │   │   │   │   └── SettingsFragment.java
│   │   │   │   └── splash/
│   │   │   │       └── SplashFragment.java
│   │   │   │
│   │   │   ├── utils/
│   │   │   │   ├── Constants.java
│   │   │   │   ├── DebounceUtils.java
│   │   │   │   ├── NetworkUtils.java
│   │   │   │   ├── Result.java
│   │   │   │   ├── SwipeToDeleteCallback.java
│   │   │   │   └── ThemeUtils.java
│   │   │   │
│   │   │   ├── adapter/
│   │   │   │   └── NewsAdapter.java
│   │   │   │
│   │   │   ├── viewmodel/
│   │   │   │   ├── NewsViewModel.java
│   │   │   │   └── NewsViewModelFactory.java
│   │   │   │
│   │   │   └── MainActivity.java
│   │   │
│   │   └── res/
│   │       ├── anim/           # Animasi navigasi
│   │       ├── color/          # Color state list
│   │       ├── drawable/       # Icon dan background
│   │       ├── drawable-night/ # Drawable untuk dark mode
│   │       ├── layout/         # Layout XML
│   │       ├── menu/           # Menu bottom navigation
│   │       ├── mipmap-*/       # Launcher icons
│   │       ├── navigation/     # Navigation graph
│   │       └── values/
│   │           ├── colors.xml
│   │           ├── dimens.xml
│   │           ├── strings.xml
│   │           └── themes.xml
│   │       └── values-night/
│   │           ├── colors.xml
│   │           └── themes.xml
│   │
│   ├── google-services.json    # Firebase config (tidak di-commit)
│   └── build.gradle.kts
│
├── gradle/
│   ├── libs.versions.toml      # Version catalog
│   └── wrapper/
│       └── gradle-wrapper.properties
│
├── build.gradle.kts
├── gradle.properties           # API Key (tidak di-commit)
├── .gitignore
└── README.md
```

---

## 💻 Persyaratan Sistem

### Development Environment
| Tool | Versi Minimum | Rekomendasi |
|------|--------------|-------------|
| **Android Studio** | Hedgehog 2023.1.1 | Ladybug 2024.2.1+ |
| **JDK** | 11 | 17 |
| **Gradle** | 8.0 | 9.2.1 |
| **Git** | 2.x | Latest |

### Device / Emulator
| Spesifikasi | Minimum | Rekomendasi |
|-------------|---------|-------------|
| **Android Version** | 7.0 (API 24) | 12.0+ (API 31+) |
| **RAM** | 2 GB | 4 GB+ |
| **Storage** | 100 MB | 500 MB+ |
| **Internet** | Diperlukan untuk data baru | - |

---

## 🚀 Cara Setup & Instalasi

### 1. Prasyarat

Pastikan sudah terinstall:
- [Android Studio](https://developer.android.com/studio) versi terbaru
- [Git](https://git-scm.com/)
- JDK 11 atau lebih baru
- Akun [GNews.io](https://gnews.io) (gratis)
- Akun [Firebase](https://console.firebase.google.com) (gratis)

---

### 2. Clone Repository

```bash
# Clone via HTTPS
git clone https://github.com/USERNAME/NNews.git

# Atau clone via SSH
git clone git@github.com:USERNAME/NNews.git

# Masuk ke direktori project
cd NNews
```

---

### 3. Buka di Android Studio

```
File → Open → Pilih folder NNews → OK
```

Tunggu hingga proses **Gradle Sync** selesai (membutuhkan koneksi internet untuk download dependencies).

---

## 🔑 Konfigurasi API

### Mendapatkan GNews API Key

1. Buka [https://gnews.io](https://gnews.io)
2. Klik **Sign Up** dan buat akun gratis
3. Verifikasi email
4. Login ke dashboard
5. Copy **API Key** yang tersedia

### Menyimpan API Key

Buka file `gradle.properties` di **root project** (bukan di folder `app/`):

```properties
# Tambahkan baris berikut
NEWS_API_KEY="masukkan_api_key_anda_di_sini"
```

> ⚠️ **Penting:** Jangan pernah commit file `gradle.properties` ke repository publik karena berisi API key rahasia. File ini sudah otomatis di-ignore oleh `.gitignore`.

### Informasi GNews Free Tier

| Item | Keterangan |
|------|-----------|
| **Request/Hari** | 100 requests |
| **Delay Berita** | 12 jam (data bukan realtime) |
| **Max Artikel/Request** | 10 artikel |
| **Bahasa** | Multi-bahasa (default: English) |
| **Upgrade** | Tersedia plan berbayar di gnews.io |

---

## 🔥 Konfigurasi Firebase

### Membuat Project Firebase

1. Buka [Firebase Console](https://console.firebase.google.com)
2. Klik **Add Project** → Beri nama `NNews`
3. Pilih apakah ingin Google Analytics → Klik **Continue**
4. Klik **Create Project** → Tunggu hingga selesai

### Menambahkan Android App

1. Di dashboard Firebase, klik ikon **Android** (tambah app)
2. Isi form:
   - **Android package name:** `com.example.nnews`
   - **App nickname:** NNews (opsional)
   - **Debug signing certificate:** (opsional, bisa diisi nanti)
3. Klik **Register app**
4. Download file `google-services.json`
5. Klik **Next** → **Next** → **Continue to Console**

### Meletakkan File Konfigurasi

```
Pindahkan google-services.json ke:

NNews/
└── app/
    └── google-services.json   ← letakkan di sini
```

### Mengaktifkan Authentication

1. Di Firebase Console, pilih project NNews
2. Klik **Authentication** di menu kiri
3. Klik tab **Sign-in method**
4. Klik **Email/Password**
5. Toggle **Enable** → Klik **Save**

> ⚠️ `google-services.json` sudah otomatis di-ignore oleh `.gitignore`. Jangan di-commit ke repository publik.

---

## ▶️ Menjalankan Aplikasi

### Menggunakan Android Studio

1. Buka Android Studio dengan project NNews
2. Pastikan device/emulator sudah terhubung
3. Pilih **Run → Run 'app'** atau tekan `Shift+F10`
4. Pilih target device dari dialog
5. Tunggu proses build dan install selesai

### Menggunakan Command Line

```bash
# Build debug APK
./gradlew assembleDebug

# Install ke device yang terhubung
./gradlew installDebug

# Build dan install sekaligus
./gradlew installDebug && adb shell am start -n com.example.nnews.debug/com.example.nnews.MainActivity

# Build release APK
./gradlew assembleRelease
```

### Lokasi APK

```
# Debug APK
app/build/outputs/apk/debug/app-debug.apk

# Release APK
app/build/outputs/apk/release/app-release.apk
```

---

## 📖 Panduan Penggunaan

### Home Screen

```
1. Buka aplikasi → Splash screen muncul → Home screen tampil
2. Geser chip kategori → Tap kategori yang diinginkan
3. Scroll ke bawah → Berita lebih banyak tampil
4. Tarik layar ke bawah → Pull-to-refresh untuk update berita
5. Tap kartu berita → Masuk ke halaman detail
```

### Search Berita

```
1. Tap kolom search di bagian atas
2. Ketik kata kunci (min 1 karakter)
3. Tunggu 600ms → Hasil pencarian muncul otomatis
4. Atau tekan Enter/tombol search → Pencarian langsung
5. Hapus teks → Kembali ke tampilan headlines
6. Tap X di search → Tutup mode pencarian
```

### Bookmark Berita

```
Cara menyimpan:
1. Di home screen → Tap ikon bookmark di pojok kartu
2. Di halaman detail → Tap ikon bookmark di toolbar

Cara melihat:
1. Tap tab Bookmark di bottom navigation

Cara menghapus:
1. Swipe kartu ke kiri/kanan → Tap Delete di dialog konfirmasi
2. Atau tap ikon bookmark → Konfirmasi hapus
3. Atau ke Settings → Clear All Bookmarks
```

### Dark Mode

```
1. Tap tab Settings di bottom navigation
2. Toggle switch Dark Mode
3. Aplikasi langsung berubah tema
4. Tema tersimpan otomatis untuk sesi berikutnya
```

### Clear Cache

```
1. Buka Settings
2. Tap "Clear Cache"
3. Konfirmasi di dialog
4. HTTP cache + Glide image cache terhapus
```

---

## 🗄️ Database & Cache

### Struktur Database

Aplikasi menggunakan **Room Database** dengan 2 tabel:

#### Tabel `bookmarks`
Menyimpan artikel yang di-bookmark oleh pengguna.

| Kolom | Tipe | Keterangan |
|-------|------|-----------|
| `url` | TEXT (PK) | URL unik artikel |
| `title` | TEXT | Judul artikel |
| `description` | TEXT | Deskripsi singkat |
| `content` | TEXT | Konten artikel |
| `image` | TEXT | URL gambar |
| `publishedAt` | TEXT | Waktu publikasi |
| `source` | TEXT (JSON) | Data sumber berita |

#### Tabel `article_cache`
Menyimpan cache berita per kategori untuk mode offline.

| Kolom | Tipe | Keterangan |
|-------|------|-----------|
| `url` | TEXT (PK) | URL unik artikel |
| `title` | TEXT | Judul artikel |
| `description` | TEXT | Deskripsi singkat |
| `content` | TEXT | Konten artikel |
| `image` | TEXT | URL gambar |
| `publishedAt` | TEXT | Waktu publikasi |
| `sourceName` | TEXT | Nama sumber |
| `sourceUrl` | TEXT | URL sumber |
| `category` | TEXT | Kategori berita |
| `cachedAt` | INTEGER | Timestamp cache disimpan |

### Strategi Cache

```
Online:
  1. Fetch dari API GNews
  2. Berhasil → Simpan ke article_cache (replace lama)
  3. Tampilkan ke UI

API Gagal / Offline:
  1. Cek tabel article_cache per category
  2. Ada data → Tampilkan ke UI + banner offline
  3. Tidak ada data → Tampilkan error state

Bookmark:
  1. Selalu dari tabel bookmarks
  2. Tidak terpengaruh status internet
```

---

## 🌙 Dark Mode

Sistem dark mode NNews menggunakan **2 file color terpisah**:

```
res/values/colors.xml       ← Warna Light Mode
res/values-night/colors.xml ← Warna Dark Mode
```

Semua nama color **identik** di kedua file — Android otomatis memilih versi yang tepat berdasarkan mode sistem.

### Palet Warna

| Token | Light Mode | Dark Mode |
|-------|-----------|-----------|
| Background | `#F5F7FF` | `#0A0E1A` |
| Surface | `#FFFFFF` | `#141824` |
| Primary | `#1565C0` | `#ADC6FF` |
| Secondary | `#00BCD4` | `#4DD0E1` |
| On Background | `#0D1117` | `#E8EAED` |

---

## 📡 Offline Mode

### Cara Kerja

```
Skenario 1 — Pertama kali online:
  App buka → Fetch API → Simpan cache → Tampilkan berita ✅

Skenario 2 — Online, cache sudah ada:
  App buka → Fetch API → Update cache → Tampilkan berita ✅

Skenario 3 — Offline, cache ada:
  App buka → Cek koneksi (offline) → Ambil cache → Tampilkan + banner ✅

Skenario 4 — Offline, belum pernah online:
  App buka → Cek koneksi (offline) → Cache kosong → Error state ❌
```

### Tips Penggunaan Offline

1. **Buka aplikasi saat online** setidaknya sekali per kategori untuk mengisi cache
2. **Ganti kategori** saat online untuk cache semua kategori
3. **Pull-to-refresh** saat online untuk memperbarui cache
4. **Bookmark** artikel penting agar selalu tersedia offline

---

## 🔧 Troubleshooting

### Build Error: `Unresolved reference`

```bash
# Solusi 1: Sync Gradle
File → Sync Project with Gradle Files

# Solusi 2: Invalidate cache
File → Invalidate Caches → Invalidate and Restart
```

### Build Error: `Resource not found`

```bash
# Clean dan rebuild
Build → Clean Project
Build → Rebuild Project
```

### Gradle Sync Gagal

```bash
# Cek versi di gradle-wrapper.properties
distributionUrl=https\://services.gradle.org/distributions/gradle-9.2.1-bin.zip

# Jika masih gagal, hapus cache Gradle
# Windows: C:\Users\USERNAME\.gradle\caches
# Mac/Linux: ~/.gradle/caches
```

### Berita Tidak Muncul

```
Kemungkinan penyebab:
1. API Key tidak valid → Cek gradle.properties
2. Rate limit (100 req/hari) → Tunggu reset jam 00.00
3. Tidak ada internet + cache kosong → Hubungkan ke internet dulu
4. google-services.json tidak ada → Download dari Firebase Console
```

### Dark Mode Tidak Tersimpan

```
Cek SharedPreferences:
- Nama file: "nnews_prefs"
- Key: "dark_mode"
- Value: boolean

Solusi: Uninstall app → Install ulang
```

### Gambar Tidak Muncul

```
Kemungkinan penyebab:
1. Tidak ada internet → Normal, placeholder tampil
2. URL gambar invalid → Data dari GNews memang tidak memiliki gambar
3. Cache Glide corrupt → Settings → Clear Cache
```

### Firebase Error

```
com.google.firebase.FirebaseException:
→ Pastikan google-services.json ada di folder app/
→ Pastikan package name di Firebase = com.example.nnews
→ Sync Gradle ulang setelah menambahkan google-services.json
```

---

## 📋 Changelog

### v1.0.0 (Latest)

**Fitur Baru:**
- ✅ Home screen dengan berita dari GNews API
- ✅ Category filter (8 kategori: General, World, Tech, Business, Sports, Science, Health, Entertainment)
- ✅ Realtime search dengan debounce 600ms
- ✅ Detail artikel dengan hero image, share, open browser
- ✅ Bookmark dengan Room Database (simpan & hapus)
- ✅ Dark mode dengan persistensi via SharedPreferences
- ✅ Offline cache per kategori dengan Room Database
- ✅ Swipe to delete bookmark dengan konfirmasi
- ✅ Pull-to-refresh
- ✅ Shimmer loading animation
- ✅ Error state, empty state, loading state
- ✅ Clear cache (HTTP + Glide)
- ✅ Clear all bookmarks
- ✅ UI overhaul dengan Material Design 3
- ✅ Greeting dinamis berdasarkan waktu
- ✅ Firebase Auth setup (WIP)

**Perbaikan Bug:**
- 🔧 Fix duplicate API call saat scroll
- 🔧 Fix search results showing wrong articles (race condition)
- 🔧 Fix detail screen tidak menampilkan gambar dan deskripsi
- 🔧 Fix category chips tidak kembali setelah search dikosongkan
- 🔧 Fix SwipeRefreshLayout hanya boleh 1 child langsung
- 🔧 Fix Room database schema conflict (fallbackToDestructiveMigration)
- 🔧 Fix ViewModel scope mismatch antar Fragment

---

## 👨‍💻 Informasi Developer

| Item | Keterangan |
|------|-----------|
| **Nama** | Fahira |
| **Program Studi** | Sistem Informasi |
| **Universitas** | Universitas Hasanuddin |
| **Jenis Proyek** | Proyek Mandiri |
| **Tahun** | 2026 |

---

## 📄 Lisensi

```
MIT License

Copyright (c) 2026 NNews - Fahira

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## 🙏 Acknowledgements

- [GNews.io](https://gnews.io) — Sumber data berita
- [Firebase](https://firebase.google.com) — Backend as a Service
- [Google Material Design](https://m3.material.io) — Design system
- [Glide](https://github.com/bumptech/glide) — Image loading library
- [Retrofit](https://square.github.io/retrofit/) — HTTP client
- [Room](https://developer.android.com/training/data-storage/room) — Local database
- [Navigation Component](https://developer.android.com/guide/navigation) — In-app navigation

---

<p align="center">
  <b>NNews</b> — Dibuat dengan ❤️ menggunakan Android Native + Java
  <br/>
  <em>Universitas Hasanuddin · Sistem Informasi · 2026</em>
</p>
