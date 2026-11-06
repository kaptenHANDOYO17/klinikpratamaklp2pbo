# Klinik Pratama: Sistem Informasi Pelayanan Pasien

Aplikasi desktop **Java Swing** untuk Tugas Proyek PBO (Implementasi GUI) 2026, tema **Klinik Pratama**.
Aplikasi menerapkan 4 pilar OOP, pola **MVC + DAO**, dan menyimpan data secara permanen di file teks.

![Beranda](docs/screenshots/beranda.png)

## Fitur

| Fitur | Keterangan |
|---|---|
| Login petugas | Akun awal `admin` / `admin123` |
| CRUD Pasien | Pasien **BPJS** (wajib No. BPJS 13 digit) dan **Umum**; validasi NIK 16 digit, NIK tidak boleh kembar |
| CRUD Dokter | Dokter **Poli Umum** dan **Poli Gigi (spesialis)**, status aktif/nonaktif praktik |
| CRUD Pemeriksaan | Pilih pasien, layanan, dokter, tindakan gigi; biaya dihitung otomatis |
| Hitung biaya (polimorfisme) | `hitungBiaya()` berbeda untuk Poli Umum vs Poli Gigi, dan BPJS vs Umum |
| Info (polimorfisme) | `tampilkanInfo()` berbeda untuk Pasien, Dokter, Petugas; struk pemeriksaan |
| Pencarian & filter | Semua tabel bisa dicari langsung saat mengetik dan difilter |
| Validasi | Pesan kesalahan yang jelas + konfirmasi sebelum hapus |

## Cara menjalankan (Windows)

1. Klik dua kali `1-cek-instal-java.bat`: cek/instal JDK 21, Git, dan ekstensi Java VS Code.
2. Klik dua kali `2-jalankan-aplikasi.bat`: kompilasi lalu jalankan aplikasi.

Lewat **VS Code**: buka folder ini, pasang *Extension Pack for Java*, buka `src/klinik/App.java`, lalu klik **Run**.
Lewat **Eclipse**: *File → Import → Existing Projects into Workspace* → pilih folder ini.

| File | Fungsi |
|---|---|
| `1-cek-instal-java.bat` | Cek & instal JDK, Git, ekstensi VS Code |
| `2-jalankan-aplikasi.bat` | Kompilasi + jalankan aplikasi |
| `3-uji-konsol.bat` | Uji model/DAO/controller lewat konsol (tanpa GUI) |
| `4-buat-jar.bat` | Membuat `dist/KlinikPratama.jar` |
| `5-upload-github.bat` | Commit dan push ke GitHub |
| `6-reset-data.bat` | Mengembalikan data ke data contoh (sebelum demo) |

## Struktur proyek

```
src/klinik/
├── App.java              # titik awal program (main)
├── UjiKonsol.java        # pengujian lewat konsol
├── model/                # Orang, Pasien, Dokter, Petugas, Pemeriksaan(+Umum/Gigi), enum
├── exception/            # ValidasiException, DataException (custom exception)
├── dao/                  # Dao<T>, FileDao<T>, PasienDao, DokterDao, PetugasDao, PemeriksaanDao
├── controller/           # Login, Beranda, Pasien, Dokter, Pemeriksaan
├── view/                 # LoginFrame, MainFrame, panel-panel, UiUtil, Tombol
└── util/                 # FormatUtil (rupiah, tanggal)
docs/                     # class diagram & screenshot
data/                     # dibuat otomatis saat aplikasi dijalankan (tidak di-upload)
```

## Class diagram

![Model](docs/class-diagram-model.png)
![MVC](docs/class-diagram-mvc.png)

## Tim

| Anggota | Bagian |
|---|---|
| Handoyo | Seluruh kode inti: model, DAO, controller, view, pengujian |
| Thufail Bima Putra Raditya | Pengujian konsol (UjiKonsol bagian 10–13: enkapsulasi, validasi dokter, polimorfisme tindakan gigi, pencarian/overloading) |
| Mikail Hikam Altiyar | Pengujian input pada Tab Pemeriksaan (input kurang, salah, kosong, dan data yang sama) + perbaikan bug data pemeriksaan yang dapat terduplikasi saat tombol Tambah ditekan setelah memilih baris tabel |
