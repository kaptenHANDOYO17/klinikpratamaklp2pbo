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

        ujiEnkapsulasiPasien();
        ujiValidasiDokter(dokterC, pasienC);
        ujiPolimorfismeTindakanGigi(periksaC);
        ujiPencarianDanOverloading(pasienC, dokterC, periksaC);
        hapusFolder(folderUji);
        System.out.println();
        System.out.println("==============================");
        System.out.println("HASIL: " + lulus + " LULUS, " + gagal + " GAGAL");
        System.out.println("==============================");
        if (gagal > 0) {
            System.exit(1);
        }
    }

        // ===== UJI TAMBAHAN ANGGOTA 2 =====

    /** 10. Enkapsulasi: validasi di setter Pasien/Orang menolak data yang tidak valid. */
    private static void ujiEnkapsulasiPasien() throws ValidasiException {
        judul("10. Enkapsulasi: validasi setter Pasien");
        LocalDate lahir = LocalDate.of(2000, 1, 1);
        cekGagal("Nama 2 huruf ditolak", () -> new Pasien("RM900", "Al", "3171000000000001", lahir,
                Pasien.LAKI_LAKI, "081200001111", "Jl. A", JenisPasien.UMUM, ""));
        cekGagal("Nama hanya spasi ditolak", () -> new Pasien("RM900", "   ", "3171000000000001", lahir,
                Pasien.LAKI_LAKI, "081200001111", "Jl. A", JenisPasien.UMUM, ""));
        cekGagal("No. telp tidak diawali 08 ditolak", () -> new Pasien("RM900", "Coba Saja", "3171000000000001",
                lahir, Pasien.LAKI_LAKI, "6281200001111", "Jl. A", JenisPasien.UMUM, ""));
        cekGagal("No. telp 14 digit ditolak", () -> new Pasien("RM900", "Coba Saja", "3171000000000001",
                lahir, Pasien.LAKI_LAKI, "08120000111122", "Jl. A", JenisPasien.UMUM, ""));
        cekGagal("NIK berisi huruf ditolak", () -> new Pasien("RM900", "Coba Saja", "31710000000000AB",
                lahir, Pasien.LAKI_LAKI, "081200001111", "Jl. A", JenisPasien.UMUM, ""));
        cekGagal("No. BPJS 12 digit ditolak", () -> new Pasien("RM900", "Coba Saja", "3171000000000001",
                lahir, Pasien.LAKI_LAKI, "081200001111", "Jl. A", JenisPasien.BPJS, "000123456789"));
        cekGagal("Jenis kelamin kosong ditolak", () -> new Pasien("RM900", "Coba Saja", "3171000000000001",
                lahir, null, "081200001111", "Jl. A", JenisPasien.UMUM, ""));
        cekGagal("Tanggal lahir lebih dari 120 tahun lalu ditolak", () -> new Pasien("RM900", "Coba Saja",
                "3171000000000001", LocalDate.now().minusYears(121), Pasien.LAKI_LAKI, "081200001111",
                "Jl. A", JenisPasien.UMUM, ""));
        cekGagal("Alamat kosong ditolak", () -> new Pasien("RM900", "Coba Saja", "3171000000000001",
                lahir, Pasien.LAKI_LAKI, "081200001111", "", JenisPasien.UMUM, ""));

        Pasien p = new Pasien("RM900", "  Coba Saja  ", "3171000000000001", lahir, Pasien.PEREMPUAN,
                "081200001111", "Jl.   Mawar    No. 1", JenisPasien.BPJS, "0001112223334");
        cek("Spasi di awal/akhir nama dibuang (trim)", p.getNama().equals("Coba Saja"));
        cek("Spasi berlebih di alamat dirapikan", p.getAlamat().equals("Jl. Mawar No. 1"));
        p.setJenisPasien(JenisPasien.UMUM);
        cek("Ganti BPJS -> Umum, No. BPJS otomatis dikosongkan", p.getNoBpjs().isEmpty());
    }
    /** 11. Validasi Dokter: No. SIP, poli, dan dokter yang salah poli. */
    private static void ujiValidasiDokter(DokterController dokterC, PasienController pasienC)
            throws ValidasiException {
        judul("11. Validasi Dokter");
        cekGagal("No. SIP 4 karakter ditolak", () -> new Dokter("DR900", "dr. Coba Saja", "081300009999",
                "Jl. B", Poli.UMUM, "SIP1", true));
        cekGagal("No. SIP berisi karakter # ditolak", () -> new Dokter("DR900", "dr. Coba Saja", "081300009999",
                "Jl. B", Poli.UMUM, "SIP#446/2024", true));
        cekGagal("Poli kosong ditolak", () -> new Dokter("DR900", "dr. Coba Saja", "081300009999",
                "Jl. B", null, "SIP.446/900/2024", true));
        cekGagal("No. SIP kembar beda huruf besar/kecil ditolak", () -> dokterC.tambah("dr. Coba Saja",
                "081300009999", "Jl. B", Poli.UMUM, "sip.446/101/2024", true));
        cekGagal("Ubah dokter yang tidak ada ditolak", () -> dokterC.ubah("DR999", "dr. Coba Saja",
                "081300009999", "Jl. B", Poli.UMUM, "SIP.446/900/2024", true));
        cekGagal("Hapus dokter tanpa memilih (null) ditolak", () -> dokterC.hapus(null));

        Pasien siti = pasienC.getById("RM002");
        Dokter dokterUmum = dokterC.getById("DR001");
        cekGagal("Dokter Poli Umum dipakai untuk pemeriksaan gigi ditolak",
                () -> new klinik.model.PemeriksaanGigi("PR900", siti, dokterUmum, LocalDate.now(),
                        "Sakit gigi", "", 0, JenisPasien.UMUM, TindakanGigi.CABUT));
    }

    /** 12. Polimorfisme: hitungBiaya() untuk SEMUA tindakan gigi, pasien Umum vs BPJS. */
    private static void ujiPolimorfismeTindakanGigi(PemeriksaanController periksaC) throws ValidasiException {
        judul("12. Polimorfisme hitungBiaya() semua tindakan gigi");
        double obat = 20000;
        for (TindakanGigi t : TindakanGigi.values()) {
            // Pasien Umum (RM002) di Poli Gigi: konsultasi Rp 100.000 + tindakan + obat
            double umum = periksaC.hitungEstimasi("RM002", "DR003", LocalDate.now(), "Sakit gigi", "", obat, t);
            double harapanUmum = 100000 + t.getHarga() + obat;
            cek("Umum + " + t + " = " + klinik.util.FormatUtil.rupiah(harapanUmum), umum == harapanUmum);

            // Pasien BPJS (RM001): ditanggung (Rp 0) kecuali tindakan estetika
            double bpjs = periksaC.hitungEstimasi("RM001", "DR003", LocalDate.now(), "Sakit gigi", "", obat, t);
            double harapanBpjs = t.isDitanggungBpjs() ? 0 : t.getHarga();
            cek("BPJS + " + t + " = " + klinik.util.FormatUtil.rupiah(harapanBpjs), bpjs == harapanBpjs);
        }
        cek("Poli Umum, pasien Umum, tanpa obat = Rp 50.000",
                periksaC.hitungEstimasi("RM002", "DR001", LocalDate.now(), "Demam", "", 0, null) == 50000);
        cek("Poli Umum, pasien BPJS, obat Rp 50.000 tetap Rp 0",
                periksaC.hitungEstimasi("RM001", "DR001", LocalDate.now(), "Demam", "", 50000, null) == 0);
        cekGagal("Biaya obat di atas Rp 10 juta ditolak",
                () -> periksaC.hitungEstimasi("RM002", "DR001", LocalDate.now(), "Demam", "", 10_000_001, null));
    }

    /** 13. Pencarian (overloading cari) dan kasus batas CRUD. */
    private static void ujiPencarianDanOverloading(PasienController pasienC, DokterController dokterC,
                                                   PemeriksaanController periksaC) {
        judul("13. Pencarian (overloading) dan kasus batas CRUD");
        cek("Cari pasien lewat NIK menemukan Siti", pasienC.cari("3171024508950002").size() == 1);
        cek("Cari 'BUDI' (huruf besar) tetap ketemu", pasienC.cari("BUDI").size() == 1);
        cek("Cari 'zzz' tidak menemukan apa pun", pasienC.cari("zzz").isEmpty());
        cek("Filter pasien Umum = 2 pasien", pasienC.cari("", JenisPasien.UMUM).size() == 2);
        cek("Filter dokter Poli Umum = 2 dokter", dokterC.cari("", Poli.UMUM).size() == 2);
        cek("Cari dokter 'drg' = 3 dokter gigi", dokterC.cari("drg").size() == 3);
        cek("Cari dokter lewat No. SIP '446/201'", dokterC.cari("446/201").size() == 1);
        cek("Cari pemeriksaan diagnosa 'karies'", periksaC.cari("karies").size() == 1);
        cek("Filter pemeriksaan Poli Umum = 2 data", periksaC.cari("", Poli.UMUM).size() == 2);
        cekGagal("Ubah pasien yang tidak ada ditolak", () -> pasienC.ubah("RM999", "Coba Saja",
                "3171000000000001", LocalDate.of(2000, 1, 1), Pasien.LAKI_LAKI, "081200001111", "Jl. A",
                JenisPasien.UMUM, ""));
        cekGagal("Hapus pasien tanpa memilih (null) ditolak", () -> pasienC.hapus(null));
        cekGagal("Hapus pemeriksaan yang tidak ada ditolak", () -> periksaC.hapus("PR999"));
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