package klinik;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import klinik.controller.BerandaController;
import klinik.controller.DokterController;
import klinik.controller.LoginController;
import klinik.controller.PasienController;
import klinik.controller.PemeriksaanController;
import klinik.dao.DataAwal;
import klinik.dao.DokterDao;
import klinik.dao.PasienDao;
import klinik.dao.PemeriksaanDao;
import klinik.dao.PetugasDao;
import klinik.exception.DataException;
import klinik.exception.ValidasiException;
import klinik.model.Dokter;
import klinik.model.JenisPasien;
import klinik.model.Orang;
import klinik.model.Pasien;
import klinik.model.Pemeriksaan;
import klinik.model.Poli;
import klinik.model.TindakanGigi;

/**
 * UJI LEWAT KONSOL (Progres 1 / Minggu 2).
 * Menguji model, DAO, dan controller TANPA tampilan.
 * Data uji ditaruh di folder sementara, jadi data asli di folder "data" tidak tersentuh.
 *
 * Jalankan: 3-uji-konsol.bat  (atau Run file ini di VS Code)
 */
public class UjiKonsol {

    private static int lulus = 0;
    private static int gagal = 0;

    public static void main(String[] args) throws Exception {
        Path folderUji = Files.createTempDirectory("klinik-uji");
        String folder = folderUji.toString();
        System.out.println("Folder data uji: " + folder);
        System.out.println();

        // ---------- 1. Siapkan DAO & data awal ----------
        PetugasDao petugasDao = new PetugasDao(folder);
        PasienDao pasienDao = new PasienDao(folder);
        DokterDao dokterDao = new DokterDao(folder);
        PemeriksaanDao pemeriksaanDao = new PemeriksaanDao(folder, pasienDao, dokterDao);
        DataAwal.isiJikaKosong(petugasDao, dokterDao, pasienDao, pemeriksaanDao);

        LoginController login = new LoginController(petugasDao);
        PasienController pasienC = new PasienController(pasienDao, pemeriksaanDao);
        DokterController dokterC = new DokterController(dokterDao, pemeriksaanDao);
        PemeriksaanController periksaC = new PemeriksaanController(pemeriksaanDao, pasienDao, dokterDao);
        BerandaController beranda = new BerandaController(pasienDao, dokterDao, petugasDao, pemeriksaanDao);

        judul("1. Data awal");
        cek("Ada 1 petugas", petugasDao.getAll().size() == 1);
        cek("Ada 4 dokter", dokterDao.getAll().size() == 4);
        cek("Ada 4 pasien", pasienDao.getAll().size() == 4);
        cek("Ada 4 pemeriksaan", pemeriksaanDao.getAll().size() == 4);

        // ---------- 2. Login ----------
        judul("2. Login");
        cek("Login admin/admin123 berhasil", login.login("admin", "admin123") != null);
        cekGagal("Login password salah ditolak", () -> login.login("admin", "salah123"));
        cekGagal("Login username kosong ditolak", () -> login.login("", "admin123"));

        // ---------- 3. Polimorfisme hitungBiaya() ----------
        judul("3. Polimorfisme hitungBiaya() (tarif umum vs spesialis)");
        // PR001 Umum+BPJS = 0 | PR002 Gigi+Umum+Tambal+obat 25rb = 100rb+150rb+25rb
        // PR003 Gigi+BPJS+Bleaching = 750rb | PR004 Umum+Umum+obat 35rb = 50rb+35rb
        cek("PR001 Poli Umum, BPJS = Rp 0", pemeriksaanDao.cariById("PR001").hitungBiaya() == 0);
        cek("PR002 Poli Gigi, Umum, Tambal = Rp 275.000", pemeriksaanDao.cariById("PR002").hitungBiaya() == 275000);
        cek("PR003 Poli Gigi, BPJS, Bleaching = Rp 750.000", pemeriksaanDao.cariById("PR003").hitungBiaya() == 750000);
        cek("PR004 Poli Umum, Umum = Rp 85.000", pemeriksaanDao.cariById("PR004").hitungBiaya() == 85000);
        cek("Total pendapatan = Rp 1.110.000", beranda.totalPendapatan() == 1110000);
        System.out.println();
        System.out.println(pemeriksaanDao.cariById("PR002").tampilkanInfo());

        judul("4. Polimorfisme tampilkanInfo() lewat List<Orang>");
        List<Orang> semua = beranda.getSemuaOrang();
        cek("List<Orang> berisi 9 orang (1 petugas + 4 dokter + 4 pasien)", semua.size() == 9);
        for (Orang o : semua) {
            System.out.println("   " + o.getPeran() + " -> " + o.getNama());
        }

        // ---------- 5. CRUD Pasien + validasi ----------
        judul("5. CRUD Pasien + validasi");
        Pasien baru = pasienC.tambah("Rudi Hartono", "3171050505050005", LocalDate.of(2005, 5, 5),
                Pasien.LAKI_LAKI, "081299990000", "Jl. Pemuda No. 9", JenisPasien.UMUM, "");
        cek("Tambah pasien -> ID otomatis RM005", baru.getId().equals("RM005"));
        cekGagal("NIK 15 digit ditolak", () -> pasienC.tambah("Coba Saja", "317105050505000",
                LocalDate.of(2000, 1, 1), Pasien.LAKI_LAKI, "081299990000", "Jl. A", JenisPasien.UMUM, ""));
        cekGagal("NIK kembar ditolak", () -> pasienC.tambah("Coba Saja", "3171050505050005",
                LocalDate.of(2000, 1, 1), Pasien.LAKI_LAKI, "081299990000", "Jl. A", JenisPasien.UMUM, ""));
        cekGagal("Pasien BPJS tanpa No. BPJS ditolak", () -> pasienC.tambah("Coba Saja", "3171050505050099",
                LocalDate.of(2000, 1, 1), Pasien.LAKI_LAKI, "081299990000", "Jl. A", JenisPasien.BPJS, ""));
        cekGagal("Tanggal lahir di masa depan ditolak", () -> pasienC.tambah("Coba Saja", "3171050505050098",
                LocalDate.now().plusDays(1), Pasien.LAKI_LAKI, "081299990000", "Jl. A", JenisPasien.UMUM, ""));
        cekGagal("Nama berisi angka ditolak", () -> pasienC.tambah("Rudi 123", "3171050505050097",
                LocalDate.of(2000, 1, 1), Pasien.LAKI_LAKI, "081299990000", "Jl. A", JenisPasien.UMUM, ""));
        cekGagal("No. telp salah format ditolak", () -> pasienC.tambah("Coba Saja", "3171050505050096",
                LocalDate.of(2000, 1, 1), Pasien.LAKI_LAKI, "12345", "Jl. A", JenisPasien.UMUM, ""));

        pasienC.ubah("RM005", "Rudi Hartono", "3171050505050005", LocalDate.of(2005, 5, 5),
                Pasien.LAKI_LAKI, "081299990000", "Jl. Pemuda No. 9", JenisPasien.BPJS, "0001112223334");
        cek("Ubah pasien RM005 menjadi BPJS", pasienDao.cariById("RM005").isPasienBpjs());
        cek("Cari 'rudi' menemukan 1 pasien", pasienC.cari("rudi").size() == 1);
        cek("Cari '' + filter BPJS menemukan 3 pasien (overloading)", pasienC.cari("", JenisPasien.BPJS).size() == 3);
        cekGagal("Hapus pasien yang punya riwayat ditolak", () -> pasienC.hapus("RM001"));
        pasienC.hapus("RM005");
        cek("Hapus pasien RM005 berhasil", pasienDao.cariById("RM005") == null);

        // ---------- 6. CRUD Dokter ----------
        judul("6. CRUD Dokter");
        Dokter dBaru = dokterC.tambah("drg. Lina Marlina", "081300001111", "Jl. Flamboyan 4",
                Poli.GIGI, "SIP.446/203/2024", true);
        cek("Tambah dokter gigi -> DR005", dBaru.getId().equals("DR005"));
        cekGagal("No. SIP kembar ditolak", () -> dokterC.tambah("dr. Coba", "081300001111", "Jl. B",
                Poli.UMUM, "SIP.446/203/2024", true));
        cekGagal("Ganti poli dokter yang punya riwayat ditolak", () -> dokterC.ubah("DR001", "dr. Rina Wulandari",
                "081311112222", "Jl. Melati No. 5", Poli.GIGI, "SIP.446/101/2024", true));
        dokterC.ubah("DR005", "drg. Lina Marlina", "081300001111", "Jl. Flamboyan 4", Poli.GIGI,
                "SIP.446/203/2024", false);
        cek("Nonaktifkan DR005", !dokterDao.cariById("DR005").isAktif());
        cek("Dokter gigi aktif ada 2", dokterC.getDokterAktif(Poli.GIGI).size() == 2);
        cekGagal("Hapus dokter yang punya riwayat ditolak", () -> dokterC.hapus("DR001"));

        // ---------- 7. CRUD Pemeriksaan ----------
        judul("7. CRUD Pemeriksaan");
        Pemeriksaan pBaru = periksaC.tambah("RM002", "DR001", LocalDate.now(), "Flu", "Common cold", 20000, null);
        cek("Tambah pemeriksaan umum pasien umum = Rp 70.000", pBaru.hitungBiaya() == 70000);
        cekGagal("Pemeriksaan gigi tanpa tindakan ditolak",
            () -> periksaC.tambah("RM002", "DR003", LocalDate.now(), "Sakit gigi", "", 0, null));
        cekGagal("Dokter tidak aktif ditolak",
            () -> periksaC.tambah("RM002", "DR005", LocalDate.now(), "Sakit gigi", "", 0, TindakanGigi.CABUT));
        cekGagal("Biaya obat negatif ditolak",
            () -> periksaC.tambah("RM002", "DR001", LocalDate.now(), "Flu", "", -5000, null));
        cekGagal("Tanggal periksa besok ditolak",
            () -> periksaC.tambah("RM002", "DR001", LocalDate.now().plusDays(1), "Flu", "", 0, null));
        cek("Estimasi gigi BPJS cabut = Rp 0",
            periksaC.hitungEstimasi("RM001", "DR003", LocalDate.now(), "Sakit gigi", "", 0, TindakanGigi.CABUT) == 0);
        periksaC.ubah(pBaru.getId(), "RM002", "DR003", LocalDate.now(), "Sakit gigi", "Pulpitis", 0, TindakanGigi.CABUT);
        cek("Ubah jadi pemeriksaan gigi + cabut = Rp 300.000", pemeriksaanDao.cariById(pBaru.getId()).hitungBiaya() == 300000);
        cek("Cari pemeriksaan filter Poli Gigi = 3 data", periksaC.cari("", Poli.GIGI).size() == 3);
        periksaC.hapus(pBaru.getId());
        cek("Hapus pemeriksaan berhasil", pemeriksaanDao.cariById(pBaru.getId()) == null);

        // ---------- 8. Data tersimpan permanen ----------
        judul("8. Data benar-benar tersimpan di file");
        PasienDao pasienDao2 = new PasienDao(folder);
        DokterDao dokterDao2 = new DokterDao(folder);
        PemeriksaanDao pemeriksaanDao2 = new PemeriksaanDao(folder, pasienDao2, dokterDao2);
        cek("Baca ulang: 4 pasien", pasienDao2.getAll().size() == 4);
        cek("Baca ulang: 5 dokter", dokterDao2.getAll().size() == 5);
        cek("Baca ulang: 4 pemeriksaan", pemeriksaanDao2.getAll().size() == 4);
        cek("Baca ulang: DR005 tetap tidak aktif", !dokterDao2.cariById("DR005").isAktif());

        // ---------- 9. Exception saat file bermasalah ----------
        judul("9. Exception handling file");
        Files.writeString(folderUji.resolve("dokter.txt"), "BARIS|RUSAK\n", java.nio.charset.StandardCharsets.UTF_8,
                java.nio.file.StandardOpenOption.APPEND);
        DokterDao dokterDao3 = new DokterDao(folder);
        cek("Baris rusak dilewati, 5 dokter tetap terbaca", dokterDao3.getAll().size() == 5);

        hapusFolder(folderUji);
        System.out.println();
        System.out.println("==============================");
        System.out.println("HASIL: " + lulus + " LULUS, " + gagal + " GAGAL");
        System.out.println("==============================");
        if (gagal > 0) {
            System.exit(1);
        }
    }

    // ===== Alat bantu uji sederhana =====

    interface AksiYangHarusGagal {
        void jalankan() throws ValidasiException, DataException;
    }

    private static void judul(String teks) {
        System.out.println();
        System.out.println("--- " + teks + " ---");
    }

    private static void cek(String nama, boolean kondisi) {
        if (kondisi) {
            lulus++;
            System.out.println("[LULUS] " + nama);
        } else {
            gagal++;
            System.out.println("[GAGAL] " + nama);
        }
    }

    private static void cekGagal(String nama, AksiYangHarusGagal aksi) {
        try {
            aksi.jalankan();
            cek(nama + " (seharusnya ditolak, tapi diterima!)", false);
        } catch (ValidasiException | DataException e) {
            cek(nama + "  -> pesan: " + e.getMessage().replace("\n", " "), true);
        }
    }

    private static void hapusFolder(Path folder) {
        try (var isi = Files.list(folder)) {
            for (Path p : isi.toList()) {
                Files.deleteIfExists(p);
            }
            Files.deleteIfExists(folder);
        } catch (IOException e) {
            System.out.println("(folder uji tidak bisa dihapus: " + e.getMessage() + ")");
        }
    }
}
