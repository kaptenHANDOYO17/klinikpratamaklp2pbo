# Checklist Demo (10 menit) - Klinik Pratama

## A. Persiapan (H-1 dan 15 menit sebelum demo)

- [ ] `2-jalankan-aplikasi.bat` sudah pernah dicoba di laptop yang dipakai demo (tidak ada error).
- [ ] `3-uji-konsol.bat` hasilnya **89 LULUS, 0 GAGAL** (screenshot untuk cadangan).
- [ ] Jalankan `6-reset-data.bat` agar data kembali ke data contoh yang rapi.
- [ ] Kode terbaru sudah di-push ke GitHub (`5-upload-github.bat`), buka halaman repo di browser.
- [ ] Buka VS Code dengan file `Pasien.java`, `PemeriksaanGigi.java`, `BerandaController.java`, `FileDao.java` di tab, siap ditunjukkan.
- [ ] Laptop dicas, notifikasi dimatikan, zoom layar 100-125%.
- [ ] Bagi peran: 1 orang menjalankan aplikasi, 1 orang menjelaskan, 1 orang menunjukkan kode.

## B. Urutan demo

| Menit | Langkah | Yang ditunjukkan / diucapkan | Hasil yang diharapkan |
|---|---|---|---|
| 0-1 | Buka aplikasi | "Tema kami Klinik Pratama, Java Swing, pola MVC, data di file" | Halaman login tampil |
| 1 | Login salah (`admin` / `salah`) | Validasi login | Pesan "Username atau password salah." |
| 1-2 | Login `admin` / `admin123` | Menu bar + sidebar (navigasi konsisten) | Beranda: kartu ringkasan + tabel tarif |
| 2 | Tunjuk tabel tarif | "Tarif Poli Umum vs Poli Gigi (spesialis), BPJS vs Umum" | - |
| 2-3 | Klik **Tampilkan Info Semua Orang** | "Polimorfisme `tampilkanInfo()` lewat `List<Orang>`" | Info Petugas, Dokter, Pasien berbeda-beda |
| 3-4 | Data Pasien → klik **Tambah** dengan form kosong | Validasi | "Nama wajib diisi." |
| 4 | Isi pasien baru, NIK `12345` → Tambah | Validasi di setter (enkapsulasi) | "NIK harus 16 digit angka." |
| 4-5 | Perbaiki NIK 16 digit, pilih BPJS, isi No. BPJS 13 digit → Tambah | CREATE | Muncul RM005 di tabel |
| 5 | Ganti Jenis Pasien ke Umum | Kolom No. BPJS otomatis nonaktif | - |
| 5 | Ketik nama di kolom **Cari**, ganti filter Jenis | Pencarian langsung + filter (overloading `cari`) | Tabel tersaring |
| 5-6 | Klik baris → ubah No. Telp → **Ubah** | UPDATE | Data di tabel berubah |
| 6 | Pilih RM001 → **Hapus** → Yes | Konfirmasi + aturan bisnis | Ditolak: "punya riwayat pemeriksaan" |
| 6 | Pilih RM005 → **Hapus** → Yes | DELETE dengan konfirmasi | Hilang dari tabel |
| 6-7 | Data Dokter → tambah dokter gigi baru | CRUD dokter (dokter bisa disesuaikan) | Muncul DR005 |
| 7 | Hilangkan centang **Aktif praktik** → Ubah | Dokter nonaktif tidak muncul di pemeriksaan | Status "Tidak aktif" |
| 7-8 | Pemeriksaan → pilih Siti (Umum), **Poli Gigi**, tindakan Cabut, keluhan, obat 20.000 → **Hitung Biaya** | Polimorfisme `hitungBiaya()` | Rp 320.000 |
| 8 | Ganti ke pasien BPJS → Hitung Biaya | BPJS ditanggung | Rp 0 |
| 8 | Klik **Tambah** | CREATE pemeriksaan | Tersimpan + total biaya |
| 8-9 | Pilih PR003 (BPJS + Bleaching) → **Lihat Struk** | Bleaching tidak ditanggung BPJS | Total Rp 750.000 |
| 9 | Perkecil jendela / geser pembatas form-tabel | Layout manager | Tampilan tetap rapi |
| 9-10 | Tutup aplikasi → buka lagi | Data permanen | Data baru masih ada |
| 10 | (Jika ditanya) buka VS Code | Tunjukkan kode 4 pilar (lihat bagian C) | - |

## C. Jika dosen minta lihat kode

| Diminta | Buka file | Tunjukkan |
|---|---|---|
| Enkapsulasi | `model/Pasien.java` | `private` + `setNik()` berisi validasi |
| Pewarisan + super | `model/Pasien.java` | `extends Orang`, `super(id, nama, noTelp, alamat)` |
| Abstract class | `model/Orang.java`, `model/Pemeriksaan.java` | `abstract` + method abstrak |
| Interface | `model/Entitas.java`, `dao/Dao.java` | diimplementasikan >1 kelas |
| Polimorfisme override | `model/PemeriksaanGigi.java` + `controller/BerandaController.java` | `hitungBiaya()` dipanggil lewat `Pemeriksaan p` |
| Overloading | `controller/PasienController.java` | dua method `cari(...)` |
| Generics & Collection | `dao/FileDao.java` | `FileDao<T extends Entitas>`, `Map<String, T>` |
| Custom exception | `exception/ValidasiException.java` | `extends Exception` |
| try-catch file | `dao/FileDao.java` | `muat()` dan `simpanKeFile()` |
| MVC | `view/PasienPanel.java` | View hanya memanggil `controller.tambah(...)` |
| Event lambda | `view/PasienPanel.java` | `pasangEvent()` |
| Uji konsol | jalankan `3-uji-konsol.bat` | 89 LULUS |

## D. Rencana cadangan

- Jika aplikasi tidak mau jalan: jalankan `1-cek-instal-java.bat`, lalu tunjukkan screenshot di laporan.
- Jika data berantakan saat demo: tutup aplikasi → `6-reset-data.bat` → buka lagi (±10 detik).
- Jika lupa urutan: ikuti tabel B dari atas.

## E. Luaran akhir yang dikumpulkan

- [ ] Link repository GitHub (semua anggota punya commit)
- [ ] Laporan Word
- [ ] Class diagram (`docs/`)
- [ ] File JAR (opsional, dari `4-buat-jar.bat`)
