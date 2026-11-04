# Panduan Belajar Proyek Klinik Pratama

File ini dibuat supaya kamu **paham seluruh isi proyek** dan siap menjawab pertanyaan dosen.
Baca berurutan. Tips: di VS Code tekan `Ctrl+Shift+V` untuk melihat file ini dalam mode baca yang rapi.

---

## 1. Gambaran besar dalam 30 detik

Aplikasi ini dipakai **petugas pendaftaran** klinik pratama untuk:

1. **Login**.
2. Mencatat **pasien** (BPJS atau Umum).
3. Mencatat **dokter** (Poli Umum atau Poli Gigi/spesialis), bisa aktif atau tidak aktif.
4. Mencatat **pemeriksaan**: pasien X diperiksa dokter Y. Aplikasi **menghitung biaya otomatis**:
   - Poli Umum lebih murah, Poli Gigi (spesialis) lebih mahal.
   - Pasien BPJS umumnya Rp 0 (ditanggung), kecuali tindakan estetika (bleaching).
5. Melihat **struk** dan **ringkasan** di beranda.

Data disimpan di folder `data/` berupa file teks, jadi **tidak hilang** saat aplikasi ditutup.

---

## 2. Alur kerja program (MVC)

Contoh alur saat petugas menekan tombol **Tambah** di halaman Pasien:

```
[PasienPanel]  (VIEW)           -> mengambil teks dari form, lalu memanggil controller
      |
      v
[PasienController] (CONTROLLER) -> membuat objek Pasien, cek NIK kembar
      |
      v
[Pasien] (MODEL)                -> setter memvalidasi: NIK 16 digit, nama wajib, dst.
      |                            jika salah -> lempar ValidasiException
      v
[PasienDao] (DAO)               -> menyimpan ke Map lalu menulis ke data/pasien.txt
      |                            jika gagal tulis -> lempar DataException
      v
kembali ke PasienPanel          -> tampilkan pesan "Berhasil" atau pesan error
```

**Kalimat untuk dosen:**
> "View hanya mengurus tampilan. View tidak pernah menyentuh file atau DAO secara langsung, semuanya lewat Controller.
> Aturan bisnis (validasi, cek NIK kembar, larangan hapus) ada di Model dan Controller. DAO hanya bertugas menyimpan dan membaca file."

---

## 3. Isi setiap folder & file

### `model/`: data dan aturannya
| File | Isi singkat |
|---|---|
| `Entitas` | **Interface**: `getId()`, `tampilkanInfo()`. Diimplementasikan `Orang` dan `Pemeriksaan` |
| `Orang` | **Abstract class** induk: id, nama, noTelp, alamat + validasinya |
| `Pasien` | Turunan Orang: NIK, tanggal lahir, jenis kelamin, jenis pasien (BPJS/Umum), No. BPJS |
| `Dokter` | Turunan Orang: poli, No. SIP, status aktif |
| `Petugas` | Turunan Orang: jabatan, username, password (untuk login) |
| `Pemeriksaan` | **Abstract class**: pasien, dokter, tanggal, keluhan, diagnosa, biaya obat, cara bayar |
| `PemeriksaanUmum` | Turunan: tarif konsultasi Rp 50.000 |
| `PemeriksaanGigi` | Turunan: tarif konsultasi Rp 100.000 + tindakan gigi |
| `JenisPasien`, `Poli`, `TindakanGigi` | **Enum** (pilihan tetap) |

### `exception/`
| File | Kapan dipakai |
|---|---|
| `ValidasiException` | Input tidak valid (nama kosong, NIK salah, dll.) |
| `DataException` | Gagal baca/tulis file, ID tidak ditemukan |

### `dao/`: penyimpanan
| File | Isi singkat |
|---|---|
| `Dao<T>` | **Interface generik** CRUD: getAll, cariById, tambah, ubah, hapus, buatIdBaru |
| `FileDao<T>` | **Abstract class generik** yang menulis/membaca file. Data di memori disimpan dalam `Map<String, T>` |
| `PasienDao`, `DokterDao`, `PetugasDao`, `PemeriksaanDao` | Cukup menentukan cara objek ↔ baris teks |
| `DataAwal` | Mengisi data contoh saat pertama kali dijalankan |

Contoh isi `data/pasien.txt` (satu baris = satu pasien, dipisah `|`):
```
RM001|Budi Santoso|3171010101900001|1990-05-12|Laki-laki|081255556666|Jl. Sudirman No. 12|BPJS|0001234567890
```

### `controller/`
| File | Tugas |
|---|---|
| `LoginController` | Cek username & password |
| `PasienController` | CRUD pasien, cari (overloading), cek NIK kembar, larang hapus pasien yang punya riwayat |
| `DokterController` | CRUD dokter, larang ganti poli/hapus dokter yang punya riwayat |
| `PemeriksaanController` | CRUD pemeriksaan, memilih kelas `PemeriksaanUmum`/`PemeriksaanGigi`, hitung estimasi |
| `BerandaController` | Ringkasan angka, total pendapatan, daftar tarif, info semua orang |

### `view/`: tampilan Swing
| File | Isi |
|---|---|
| `LoginFrame` | Halaman login (tampilan 1) |
| `MainFrame` | Jendela utama: **menu bar** + **sidebar** + CardLayout (tampilan 2) |
| `BerandaPanel` | Kartu ringkasan + tabel tarif Umum vs Spesialis |
| `PasienPanel`, `DokterPanel`, `PemeriksaanPanel` | Form + tabel + pencarian (tampilan 3, 4, 5) |
| `HalamanData` | Interface: setiap halaman punya `muatUlang()` |
| `UiUtil` | Fungsi bantu: warna, dialog pesan, tabel, form, spinner |
| `Tombol` | Tombol berwarna buatan sendiri |

---

## 4. Empat pilar OOP: di mana letaknya?

### a. Enkapsulasi
- Semua atribut **private**. Contoh di `Pasien`: `private String nik;`
- Akses lewat **getter/setter yang berisi validasi**. Contoh `Pasien.setNik()`:
  ```java
  if (!bersih.matches("\\d{16}")) {
      throw new ValidasiException("NIK harus 16 digit angka.");
  }
  ```
- Contoh lain: `Pemeriksaan.setBiayaObat()` menolak angka negatif.

> "Data tidak bisa diisi sembarangan dari luar kelas. Semua harus lewat setter, dan setter yang menjaga datanya tetap benar."

### b. Pewarisan (inheritance)
- **Hierarki 1:** `Orang` → `Pasien`, `Dokter`, `Petugas`
- **Hierarki 2:** `Pemeriksaan` → `PemeriksaanUmum`, `PemeriksaanGigi`
- Kata kunci `super` di konstruktor, contoh di `Pasien`:
  ```java
  super(id, nama, noTelp, alamat);   // isi atribut milik Orang
  ```

### c. Abstraksi
- **Abstract class:** `Orang` (method abstrak `getPeran()`, `tampilkanInfo()`), `Pemeriksaan` (`hitungBiaya()`, `getNamaLayanan()`, dll.), `FileDao<T>` (`keKolom()`, `dariKolom()`).
- **Interface yang diimplementasikan >1 kelas:**
  - `Entitas` → `Orang` (Pasien/Dokter/Petugas) dan `Pemeriksaan`
  - `Dao<T>` → `FileDao` (lalu 4 DAO)
  - `HalamanData` → 4 panel tampilan

### d. Polimorfisme
- **Overriding dipanggil lewat referensi induk.** Contoh di `BerandaController.totalPendapatan()`:
  ```java
  for (Pemeriksaan p : pemeriksaanDao.getAll()) {
      total += p.hitungBiaya();   // otomatis versi Umum atau Gigi
  }
  ```
  dan `getInfoSemuaOrang()` memanggil `o.tampilkanInfo()` untuk `List<Orang>` yang berisi Pasien, Dokter, Petugas.
  Di aplikasi bisa dilihat lewat tombol **"Tampilkan Info Semua Orang"** di Beranda.
- **Overloading:** method `cari` di setiap controller:
  ```java
  public List<Pasien> cari(String kataKunci)                    // cari biasa
  public List<Pasien> cari(String kataKunci, JenisPasien jenis) // cari + filter
  ```
  Konstruktor `DataException(String)` dan `DataException(String, Throwable)` juga contoh overloading.

---

## 5. Collection & Generics
- `List<Pasien>`, `List<Orang>`, `List<String[]>` dipakai di controller.
- `Map<String, T>` (LinkedHashMap) di `FileDao` untuk menyimpan data berdasarkan ID.
- `Map<String, HalamanData>` di `MainFrame` untuk daftar halaman.
- Kelas generik: `Dao<T extends Entitas>` dan `FileDao<T extends Entitas>`.

## 6. Exception handling
- **Custom exception:** `ValidasiException` dan `DataException` (keduanya *checked*, wajib ditangkap).
- `try-catch` pada operasi file di `FileDao.muat()` dan `simpanKeFile()`.
- `try-catch` di setiap tombol view, contoh `PasienPanel.tambahData()`:
  ```java
  try { ... controller.tambah(...) ... }
  catch (ValidasiException e) { UiUtil.pesanError(this, e.getMessage()); }
  catch (DataException e)     { UiUtil.pesanErrorData(this, e.getMessage()); }
  ```
- Jika simpan ke file gagal, perubahan di memori **dibatalkan** (rollback) supaya data tetap konsisten.
- Baris file yang rusak dilewati, aplikasi tidak crash (diuji di `UjiKonsol` bagian 9).

---

## 7. Aturan biaya (inti tema "tarif umum vs spesialis")

| Layanan | Pasien Umum | Pasien BPJS |
|---|---|---|
| Poli Umum | 50.000 + obat | Rp 0 |
| Poli Gigi (spesialis) | 100.000 + tindakan + obat | Rp 0, **kecuali** Bleaching (bayar Rp 750.000) |

Harga tindakan gigi: Konsultasi saja 0, Tambal 150.000, Cabut 200.000, Scaling 250.000, Bleaching 750.000.

Contoh hitung: Siti (Umum) ke Poli Gigi, tambal, obat 25.000 → 100.000 + 150.000 + 25.000 = **Rp 275.000**.

---

## 8. Daftar validasi (bisa didemokan)

| Data | Aturan |
|---|---|
| Nama | Wajib, minimal 3 huruf, tidak boleh angka |
| NIK | 16 digit angka, tidak boleh sama dengan pasien lain |
| No. Telp | Diawali 08, total 10-13 digit |
| Tanggal lahir | Tidak boleh di masa depan, maksimal 120 tahun lalu |
| Jenis kelamin | Wajib dipilih |
| No. BPJS | Wajib 13 digit **jika** pasien BPJS (kolomnya nonaktif untuk pasien Umum) |
| No. SIP dokter | Wajib, minimal 5 karakter, tidak boleh kembar |
| Pemeriksaan | Pasien & dokter wajib, keluhan wajib, tanggal tidak boleh di masa depan, biaya obat tidak negatif, tindakan wajib untuk Poli Gigi |
| Dokter | Dokter tidak aktif tidak bisa dipilih untuk pemeriksaan baru |
| Hapus | Selalu ada konfirmasi. Pasien/dokter yang sudah punya riwayat pemeriksaan **tidak bisa dihapus** |

---

## 9. Keputusan desain (bukti berpikir kritis)

1. **Penyimpanan file teks, bukan database.** Tugas membolehkan "basis data atau file". File tidak butuh instal MySQL atau driver, jadi aplikasi pasti jalan di laptop mana pun saat demo.
2. **`caraBayar` disimpan di Pemeriksaan.** Jika pasien pindah dari Umum ke BPJS, biaya kunjungan lama **tidak ikut berubah**.
3. **Larangan hapus pasien/dokter yang punya riwayat.** Agar tidak ada pemeriksaan "yatim" yang pasien/dokternya hilang. Untuk dokter, solusinya: nonaktifkan.
4. **Larangan mengganti poli dokter yang punya riwayat.** Pemeriksaan gigi lama tidak boleh tiba-tiba dilayani "dokter umum".
5. **Validasi di setter (model),** bukan hanya di form, sehingga data dari file pun ikut divalidasi.
6. **Rollback saat simpan gagal.** Data di memori kembali seperti semula.
7. **Tombol "Hitung Biaya"** memakai objek sementara (tidak disimpan), jadi pasien tahu biayanya sebelum didaftarkan.

Keterbatasan yang jujur (bisa disebut sebagai pengembangan berikutnya): password masih disimpan sebagai teks biasa (sebaiknya di-*hash*), satu file hanya aman dipakai satu aplikasi dalam satu waktu, dan belum ada fitur cetak ke printer.

---

## 10. Latihan pertanyaan dosen

**T: Mana bukti polimorfisme?**
J: `hitungBiaya()` di-override `PemeriksaanUmum` dan `PemeriksaanGigi`, lalu dipanggil lewat referensi `Pemeriksaan` di `BerandaController.totalPendapatan()` dan di tabel pemeriksaan. `tampilkanInfo()` dipanggil lewat `List<Orang>`.

**T: Kenapa `Orang` abstract?**
J: Tidak ada "orang" yang hanya orang. Selalu pasien, dokter, atau petugas. Jadi `Orang` tidak boleh dibuat objeknya, hanya diwarisi.

**T: Bedanya abstract class dan interface di proyek ini?**
J: Abstract class `Orang` punya atribut dan kode bersama (validasi nama, telp). Interface `Entitas` hanya kontrak (`getId`, `tampilkanInfo`) yang bisa dipakai kelas yang tidak sekeluarga (Orang dan Pemeriksaan).

**T: Kenapa pakai custom exception?**
J: Supaya pesan error jelas dan bisa dibedakan: `ValidasiException` untuk kesalahan input pengguna, `DataException` untuk masalah file.

**T: Bagaimana data tersimpan permanen?**
J: `FileDao.simpanKeFile()` menulis seluruh data ke file `.txt` di folder `data` setiap kali tambah/ubah/hapus. Saat aplikasi dibuka, `muat()` membacanya kembali.

**T: Bagaimana ID dibuat?**
J: `FileDao.buatIdBaru()` mencari angka terbesar lalu ditambah 1, contoh RM004 → RM005.

**T: Kenapa tampilan tetap rapi saat diperkecil?**
J: Memakai layout manager: `BorderLayout` untuk kerangka, `GridBagLayout` untuk form, `CardLayout` untuk ganti halaman, `JSplitPane` agar batas form dan tabel bisa digeser, dan tabel di dalam `JScrollPane`.

**T: Di mana event handling-nya?**
J: Di method `pasangEvent()` setiap panel. Memakai lambda, contoh `btnTambah.addActionListener(e -> tambahData());`. Setiap aksi punya method sendiri (tambahData, ubahData, hapusData, ...), tidak ditumpuk di satu method.

**T: Komponen input apa saja?**
J: JTextField, JTextArea, JPasswordField, JComboBox, JRadioButton (jenis kelamin, layanan), JCheckBox (aktif praktik), JSpinner tanggal (pengganti date picker) dan JSpinner angka (biaya obat).

---

## 11. Cara menjalankan & menguji

- Aplikasi: `2-jalankan-aplikasi.bat` atau Run `App.java` di VS Code.
- Uji konsol (bukti Progres 1, Minggu 2): `3-uji-konsol.bat` → hasilnya harus **89 LULUS, 0 GAGAL**.
- Reset data contoh: `6-reset-data.bat`.

## 12. Untuk anggota kelompok lain (dekorasi/tambahan)

Ketentuan tugas: **setiap anggota wajib punya commit** pada bagiannya. Ide pekerjaan aman untuk anggota 2 & 3:
- Ganti warna tema di `UiUtil` (konstanta `WARNA_...`).
- Tambah ikon/logo pada `LoginFrame` dan sidebar `MainFrame`.
- Tambah kolom/teks bantuan, atau perbaiki README dan class diagram.
- Tambah kasus uji baru di `UjiKonsol`.

Alur Git untuk anggota lain:
```
git clone <url-repo>
(ubah kode)
git add .
git commit -m "Menambah logo di halaman login"
git pull
git push
```
Pemilik repo harus mengundang mereka dulu: GitHub → repo → *Settings → Collaborators → Add people*.
