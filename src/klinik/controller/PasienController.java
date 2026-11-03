package klinik.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import klinik.dao.PasienDao;
import klinik.dao.PemeriksaanDao;
import klinik.exception.DataException;
import klinik.exception.ValidasiException;
import klinik.model.JenisPasien;
import klinik.model.Pasien;
import klinik.model.Pemeriksaan;

/**
 * Controller Pasien: penghubung antara tampilan (PasienPanel) dan data (PasienDao).
 * View tidak pernah mengakses DAO secara langsung.
 */
public class PasienController {

    private final PasienDao pasienDao;
    private final PemeriksaanDao pemeriksaanDao;

    public PasienController(PasienDao pasienDao, PemeriksaanDao pemeriksaanDao) {
        this.pasienDao = pasienDao;
        this.pemeriksaanDao = pemeriksaanDao;
    }

    public List<Pasien> getAll() {
        return pasienDao.getAll();
    }

    public Pasien getById(String id) {
        return pasienDao.cariById(id);
    }

    // ===== OVERLOADING: dua method "cari" dengan parameter berbeda =====

    /** Cari pasien berdasarkan kata kunci saja (semua jenis pasien). */
    public List<Pasien> cari(String kataKunci) {
        return cari(kataKunci, null);
    }

    /** Cari pasien berdasarkan kata kunci DAN jenis pasien (null = semua jenis). */
    public List<Pasien> cari(String kataKunci, JenisPasien jenis) {
        String kunci = (kataKunci == null) ? "" : kataKunci.trim().toLowerCase();
        List<Pasien> hasil = new ArrayList<>();
        for (Pasien p : pasienDao.getAll()) {
            boolean cocokKata = kunci.isEmpty()
                    || p.getId().toLowerCase().contains(kunci)
                    || p.getNama().toLowerCase().contains(kunci)
                    || p.getNik().contains(kunci)
                    || p.getNoBpjs().contains(kunci);
            boolean cocokJenis = (jenis == null) || p.getJenisPasien() == jenis;
            if (cocokKata && cocokJenis) {
                hasil.add(p);
            }
        }
        return hasil;
    }

    // ===== CRUD =====

    public Pasien tambah(String nama, String nik, LocalDate tanggalLahir, String jenisKelamin,
                         String noTelp, String alamat, JenisPasien jenis, String noBpjs)
            throws ValidasiException, DataException {
        String idBaru = pasienDao.buatIdBaru();
        // validasi isi field dilakukan oleh setter di kelas Pasien
        Pasien pasien = new Pasien(idBaru, nama, nik, tanggalLahir, jenisKelamin, noTelp, alamat, jenis, noBpjs);
        cekNikBelumDipakai(pasien.getNik(), null);
        pasienDao.tambah(pasien);
        return pasien;
    }

    public Pasien ubah(String id, String nama, String nik, LocalDate tanggalLahir, String jenisKelamin,
                       String noTelp, String alamat, JenisPasien jenis, String noBpjs)
            throws ValidasiException, DataException {
        if (id == null || pasienDao.cariById(id) == null) {
            throw new ValidasiException("Pilih pasien di tabel terlebih dahulu.");
        }
        Pasien pasien = new Pasien(id, nama, nik, tanggalLahir, jenisKelamin, noTelp, alamat, jenis, noBpjs);
        cekNikBelumDipakai(pasien.getNik(), id);
        cekTanggalLahirSesuaiRiwayat(id, pasien.getTanggalLahir());
        pasienDao.ubah(pasien);
        pemeriksaanDao.muat(); // agar data pemeriksaan memakai data pasien yang terbaru
        return pasien;
    }

    public void hapus(String id) throws ValidasiException, DataException {
        if (id == null || pasienDao.cariById(id) == null) {
            throw new ValidasiException("Pilih pasien di tabel terlebih dahulu.");
        }
        if (pemeriksaanDao.adaUntukPasien(id)) {
            throw new ValidasiException("Pasien tidak bisa dihapus karena sudah memiliki riwayat pemeriksaan.\n"
                    + "Hapus dulu data pemeriksaannya jika memang ingin menghapus pasien ini.");
        }
        pasienDao.hapus(id);
    }

    // ===== Aturan bisnis tambahan =====

    /** NIK tidak boleh sama dengan pasien lain. idDikecualikan = pasien yang sedang diubah. */
    private void cekNikBelumDipakai(String nik, String idDikecualikan) throws ValidasiException {
        for (Pasien p : pasienDao.getAll()) {
            if (p.getNik().equals(nik) && !p.getId().equals(idDikecualikan)) {
                throw new ValidasiException("NIK " + nik + " sudah terdaftar atas nama " + p.getNama() + ".");
            }
        }
    }

    /** Tanggal lahir baru tidak boleh lebih baru dari tanggal pemeriksaan yang sudah ada. */
    private void cekTanggalLahirSesuaiRiwayat(String idPasien, LocalDate tanggalLahir) throws ValidasiException {
        for (Pemeriksaan p : pemeriksaanDao.getAll()) {
            if (p.getPasien().getId().equals(idPasien) && p.getTanggal().isBefore(tanggalLahir)) {
                throw new ValidasiException("Tanggal lahir tidak sesuai: pasien sudah punya pemeriksaan pada "
                        + p.getTanggal() + ".");
            }
        }
    }
}
