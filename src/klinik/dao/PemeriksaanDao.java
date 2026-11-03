package klinik.dao;

import java.time.LocalDate;
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

/**
 * Menyimpan data pemeriksaan di file data/pemeriksaan.txt. ID: PR001, PR002, ...
 * Di file hanya disimpan ID pasien & ID dokter; saat dibaca, ID itu dicari
 * lewat PasienDao dan DokterDao agar menjadi objek lagi.
 */
public class PemeriksaanDao extends FileDao<Pemeriksaan> {

    private final PasienDao pasienDao;
    private final DokterDao dokterDao;

    public PemeriksaanDao(String folderData, PasienDao pasienDao, DokterDao dokterDao) throws DataException {
        super(folderData, "pemeriksaan.txt", "PR");
        this.pasienDao = pasienDao;
        this.dokterDao = dokterDao;
        muat(); // dipanggil setelah pasienDao & dokterDao siap
    }

    @Override
    protected String[] keKolom(Pemeriksaan p) {
        String tindakan = "-";
        if (p instanceof PemeriksaanGigi) {
            tindakan = ((PemeriksaanGigi) p).getTindakan().name();
        }
        return new String[] {
            p.getId(), p.getPoliLayanan().name(), p.getPasien().getId(), p.getDokter().getId(),
            p.getTanggal().toString(), p.getKeluhan(), p.getDiagnosa(),
            String.valueOf(p.getBiayaObat()), p.getCaraBayar().name(), tindakan
        };
    }

    @Override
    protected Pemeriksaan dariKolom(String[] k) throws ValidasiException {
        Pasien pasien = pasienDao.cariById(k[2]);
        Dokter dokter = dokterDao.cariById(k[3]);
        if (pasien == null || dokter == null) {
            throw new ValidasiException("pasien/dokter dengan ID " + k[2] + "/" + k[3] + " tidak ditemukan");
        }
        LocalDate tanggal = LocalDate.parse(k[4]);
        double biayaObat = Double.parseDouble(k[7]);
        JenisPasien caraBayar = JenisPasien.valueOf(k[8]);

        if (Poli.valueOf(k[1]) == Poli.GIGI) {
            return new PemeriksaanGigi(k[0], pasien, dokter, tanggal, k[5], k[6], biayaObat, caraBayar,
                    TindakanGigi.valueOf(k[9]));
        }
        return new PemeriksaanUmum(k[0], pasien, dokter, tanggal, k[5], k[6], biayaObat, caraBayar);
    }

    /** True jika pasien ini sudah punya riwayat pemeriksaan. */
    public boolean adaUntukPasien(String idPasien) {
        for (Pemeriksaan p : getAll()) {
            if (p.getPasien().getId().equals(idPasien)) {
                return true;
            }
        }
        return false;
    }

    /** True jika dokter ini sudah pernah menangani pemeriksaan. */
    public boolean adaUntukDokter(String idDokter) {
        for (Pemeriksaan p : getAll()) {
            if (p.getDokter().getId().equals(idDokter)) {
                return true;
            }
        }
        return false;
    }
}
