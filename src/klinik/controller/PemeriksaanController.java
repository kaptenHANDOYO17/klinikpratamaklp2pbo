package klinik.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import klinik.dao.DokterDao;
import klinik.dao.PasienDao;
import klinik.dao.PemeriksaanDao;
import klinik.exception.DataException;
import klinik.exception.ValidasiException;
import klinik.model.Dokter;
import klinik.model.JenisPasien;
import klinik.model.Pasien;
import klinik.model.Pemeriksaan;
import klinik.model.PemeriksaanGigi;
import klinik.model.PemeriksaanUmum;
import klinik.model.Poli;
import klinik.model.TindakanGigi;

/** Controller Pemeriksaan: mengatur pendaftaran pemeriksaan dan perhitungan biaya. */
public class PemeriksaanController {

    private final PemeriksaanDao pemeriksaanDao;
    private final PasienDao pasienDao;
    private final DokterDao dokterDao;

    public PemeriksaanController(PemeriksaanDao pemeriksaanDao, PasienDao pasienDao, DokterDao dokterDao) {
        this.pemeriksaanDao = pemeriksaanDao;
        this.pasienDao = pasienDao;
        this.dokterDao = dokterDao;
    }

    public List<Pemeriksaan> getAll() {
        return pemeriksaanDao.getAll();
    }

    public Pemeriksaan getById(String id) {
        return pemeriksaanDao.cariById(id);
    }

    // ===== OVERLOADING =====

    public List<Pemeriksaan> cari(String kataKunci) {
        return cari(kataKunci, null);
    }

    public List<Pemeriksaan> cari(String kataKunci, Poli poli) {
        String kunci = (kataKunci == null) ? "" : kataKunci.trim().toLowerCase();
        List<Pemeriksaan> hasil = new ArrayList<>();
        for (Pemeriksaan p : pemeriksaanDao.getAll()) {
            boolean cocokKata = kunci.isEmpty()
                    || p.getId().toLowerCase().contains(kunci)
                    || p.getPasien().getNama().toLowerCase().contains(kunci)
                    || p.getDokter().getNama().toLowerCase().contains(kunci)
                    || p.getDiagnosa().toLowerCase().contains(kunci);
            boolean cocokPoli = (poli == null) || p.getPoliLayanan() == poli;
            if (cocokKata && cocokPoli) {
                hasil.add(p);
            }
        }
        return hasil;
    }

    // ===== CRUD =====

    public Pemeriksaan tambah(String idPasien, String idDokter, LocalDate tanggal, String keluhan,
                              String diagnosa, double biayaObat, TindakanGigi tindakan)
            throws ValidasiException, DataException {
        Pasien pasien = ambilPasien(idPasien);
        Dokter dokter = ambilDokter(idDokter);
        if (!dokter.isAktif()) {
            throw new ValidasiException(dokter.getNama() + " sedang tidak aktif praktik.");
        }
        Pemeriksaan baru = buatObjek(pemeriksaanDao.buatIdBaru(), pasien, dokter, tanggal, keluhan,
                diagnosa, biayaObat, pasien.getJenisPasien(), tindakan);
        pemeriksaanDao.tambah(baru);
        return baru;
    }

    public Pemeriksaan ubah(String id, String idPasien, String idDokter, LocalDate tanggal, String keluhan,
                            String diagnosa, double biayaObat, TindakanGigi tindakan)
            throws ValidasiException, DataException {
        Pemeriksaan lama = (id == null) ? null : pemeriksaanDao.cariById(id);
        if (lama == null) {
            throw new ValidasiException("Pilih data pemeriksaan di tabel terlebih dahulu.");
        }
        Pasien pasien = ambilPasien(idPasien);
        Dokter dokter = ambilDokter(idDokter);
        // dokter lama boleh tetap dipakai walau sekarang tidak aktif
        boolean gantiDokter = !lama.getDokter().getId().equals(dokter.getId());
        if (gantiDokter && !dokter.isAktif()) {
            throw new ValidasiException(dokter.getNama() + " sedang tidak aktif praktik.");
        }
        // cara bayar lama dipertahankan jika pasiennya sama
        JenisPasien caraBayar = lama.getPasien().getId().equals(pasien.getId())
                ? lama.getCaraBayar() : pasien.getJenisPasien();
        Pemeriksaan baru = buatObjek(id, pasien, dokter, tanggal, keluhan, diagnosa, biayaObat, caraBayar, tindakan);
        pemeriksaanDao.ubah(baru);
        return baru;
    }

    public void hapus(String id) throws ValidasiException, DataException {
        if (id == null || pemeriksaanDao.cariById(id) == null) {
            throw new ValidasiException("Pilih data pemeriksaan di tabel terlebih dahulu.");
        }
        pemeriksaanDao.hapus(id);
    }

    /** Menghitung perkiraan biaya TANPA menyimpan data (tombol "Hitung Biaya"). */
    public double hitungEstimasi(String idPasien, String idDokter, LocalDate tanggal, String keluhan,
                                 String diagnosa, double biayaObat, TindakanGigi tindakan)
            throws ValidasiException {
        Pasien pasien = ambilPasien(idPasien);
        Dokter dokter = ambilDokter(idDokter);
        Pemeriksaan sementara = buatObjek("ESTIMASI", pasien, dokter, tanggal, keluhan, diagnosa,
                biayaObat, pasien.getJenisPasien(), tindakan);
        return sementara.hitungBiaya(); // polimorfisme: Umum/Gigi menghitung dengan cara masing-masing
    }

    // ===== Bantuan =====

    /** Memilih kelas anak yang tepat berdasarkan poli dokter. */
    private Pemeriksaan buatObjek(String id, Pasien pasien, Dokter dokter, LocalDate tanggal, String keluhan,
                                  String diagnosa, double biayaObat, JenisPasien caraBayar,
                                  TindakanGigi tindakan) throws ValidasiException {
        if (dokter.getPoli() == Poli.GIGI) {
            return new PemeriksaanGigi(id, pasien, dokter, tanggal, keluhan, diagnosa, biayaObat, caraBayar, tindakan);
        }
        return new PemeriksaanUmum(id, pasien, dokter, tanggal, keluhan, diagnosa, biayaObat, caraBayar);
    }

    private Pasien ambilPasien(String idPasien) throws ValidasiException {
        Pasien pasien = (idPasien == null) ? null : pasienDao.cariById(idPasien);
        if (pasien == null) {
            throw new ValidasiException("Pilih pasien terlebih dahulu.");
        }
        return pasien;
    }

    private Dokter ambilDokter(String idDokter) throws ValidasiException {
        Dokter dokter = (idDokter == null) ? null : dokterDao.cariById(idDokter);
        if (dokter == null) {
            throw new ValidasiException("Pilih dokter terlebih dahulu.");
        }
        return dokter;
    }
}
