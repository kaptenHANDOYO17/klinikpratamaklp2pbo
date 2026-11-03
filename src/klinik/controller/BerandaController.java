package klinik.controller;

import java.util.ArrayList;
import java.util.List;
import klinik.dao.DokterDao;
import klinik.dao.PasienDao;
import klinik.dao.PemeriksaanDao;
import klinik.dao.PetugasDao;
import klinik.model.Dokter;
import klinik.model.JenisPasien;
import klinik.model.Orang;
import klinik.model.Pasien;
import klinik.model.Pemeriksaan;
import klinik.model.PemeriksaanGigi;
import klinik.model.PemeriksaanUmum;
import klinik.model.Poli;
import klinik.model.TindakanGigi;
import klinik.util.FormatUtil;

/** Controller untuk halaman Beranda: ringkasan angka, daftar tarif, dan info semua orang. */
public class BerandaController {

    private final PasienDao pasienDao;
    private final DokterDao dokterDao;
    private final PetugasDao petugasDao;
    private final PemeriksaanDao pemeriksaanDao;

    public BerandaController(PasienDao pasienDao, DokterDao dokterDao,
                             PetugasDao petugasDao, PemeriksaanDao pemeriksaanDao) {
        this.pasienDao = pasienDao;
        this.dokterDao = dokterDao;
        this.petugasDao = petugasDao;
        this.pemeriksaanDao = pemeriksaanDao;
    }

    public int jumlahPasien(JenisPasien jenis) {
        int jumlah = 0;
        for (Pasien p : pasienDao.getAll()) {
            if (p.getJenisPasien() == jenis) {
                jumlah++;
            }
        }
        return jumlah;
    }

    public int jumlahDokter(Poli poli) {
        int jumlah = 0;
        for (Dokter d : dokterDao.getAll()) {
            if (d.getPoli() == poli) {
                jumlah++;
            }
        }
        return jumlah;
    }

    public int jumlahPemeriksaan() {
        return pemeriksaanDao.getAll().size();
    }

    /** POLIMORFISME: setiap p bisa Umum/Gigi, hitungBiaya() otomatis memakai versi yang tepat. */
    public double totalPendapatan() {
        double total = 0;
        for (Pemeriksaan p : pemeriksaanDao.getAll()) {
            total += p.hitungBiaya();
        }
        return total;
    }

    /** Menggabungkan pasien, dokter, dan petugas ke dalam satu List bertipe induk (Orang). */
    public List<Orang> getSemuaOrang() {
        List<Orang> semua = new ArrayList<>();
        semua.addAll(petugasDao.getAll());
        semua.addAll(dokterDao.getAll());
        semua.addAll(pasienDao.getAll());
        return semua;
    }

    /** POLIMORFISME: tampilkanInfo() dipanggil lewat referensi Orang. */
    public String getInfoSemuaOrang() {
        StringBuilder sb = new StringBuilder();
        for (Orang o : getSemuaOrang()) {
            sb.append(o.tampilkanInfo()).append("\n");
        }
        return sb.toString();
    }

    /** Baris-baris tabel tarif: {Layanan, Tarif Pasien Umum, Tarif Pasien BPJS}. */
    public List<String[]> getDaftarTarif() {
        List<String[]> daftar = new ArrayList<>();
        daftar.add(new String[] {"Konsultasi Poli Umum",
            FormatUtil.rupiah(PemeriksaanUmum.TARIF_KONSULTASI), "Ditanggung (Rp 0)"});
        daftar.add(new String[] {"Konsultasi Poli Gigi (Spesialis)",
            FormatUtil.rupiah(PemeriksaanGigi.TARIF_KONSULTASI), "Ditanggung (Rp 0)"});
        for (TindakanGigi t : TindakanGigi.values()) {
            String bpjs = t.isDitanggungBpjs() ? "Ditanggung (Rp 0)" : FormatUtil.rupiah(t.getHarga()) + " (bayar sendiri)";
            daftar.add(new String[] {"Tindakan Gigi: " + t, FormatUtil.rupiah(t.getHarga()), bpjs});
        }
        daftar.add(new String[] {"Obat", "Sesuai resep", "Ditanggung (Rp 0)"});
        return daftar;
    }
}
