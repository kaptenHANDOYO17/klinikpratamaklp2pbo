package klinik.dao;

import java.time.LocalDate;
import klinik.exception.DataException;
import klinik.exception.ValidasiException;
import klinik.model.Dokter;
import klinik.model.JenisPasien;
import klinik.model.Pasien;
import klinik.model.PemeriksaanGigi;
import klinik.model.PemeriksaanUmum;
import klinik.model.Petugas;
import klinik.model.Poli;
import klinik.model.TindakanGigi;

/**
 * Mengisi data contoh saat aplikasi pertama kali dijalankan (file data masih kosong),
 * supaya aplikasi langsung bisa dicoba dan didemokan.
 */
public final class DataAwal {

    private DataAwal() {
    }

    public static void isiJikaKosong(PetugasDao petugasDao, DokterDao dokterDao,
                                     PasienDao pasienDao, PemeriksaanDao pemeriksaanDao)
            throws DataException, ValidasiException {

        // Akun login default
        if (petugasDao.getAll().isEmpty()) {
            petugasDao.tambah(new Petugas("PT001", "Admin Klinik", "081234567890",
                    "Jl. Merdeka No. 1", "Admin Pendaftaran", "admin", "admin123"));
        }

        // Data contoh hanya diisi jika SEMUA data masih kosong
        boolean semuaKosong = dokterDao.getAll().isEmpty()
                && pasienDao.getAll().isEmpty()
                && pemeriksaanDao.getAll().isEmpty();
        if (!semuaKosong) {
            return;
        }

        Dokter rina = new Dokter("DR001", "dr. Rina Wulandari", "081311112222", "Jl. Melati No. 5",
                Poli.UMUM, "SIP.446/101/2024", true);
        Dokter ahmad = new Dokter("DR002", "dr. Ahmad Fauzi", "081322223333", "Jl. Kenanga No. 8",
                Poli.UMUM, "SIP.446/102/2024", true);
        Dokter maya = new Dokter("DR003", "drg. Maya Sari", "081333334444", "Jl. Anggrek No. 2",
                Poli.GIGI, "SIP.446/201/2024", true);
        Dokter hendra = new Dokter("DR004", "drg. Hendra Gunawan, Sp.KG", "081344445555", "Jl. Mawar No. 10",
                Poli.GIGI, "SIP.446/202/2024", true);
        dokterDao.tambah(rina);
        dokterDao.tambah(ahmad);
        dokterDao.tambah(maya);
        dokterDao.tambah(hendra);

        Pasien budi = new Pasien("RM001", "Budi Santoso", "3171010101900001", LocalDate.of(1990, 5, 12),
                Pasien.LAKI_LAKI, "081255556666", "Jl. Sudirman No. 12", JenisPasien.BPJS, "0001234567890");
        Pasien siti = new Pasien("RM002", "Siti Aminah", "3171024508950002", LocalDate.of(1995, 8, 5),
                Pasien.PEREMPUAN, "081266667777", "Jl. Diponegoro No. 3", JenisPasien.UMUM, "");
        Pasien andi = new Pasien("RM003", "Andi Pratama", "3171031203000003", LocalDate.of(2000, 3, 12),
                Pasien.LAKI_LAKI, "081277778888", "Jl. Gatot Subroto No. 7", JenisPasien.BPJS, "0009876543210");
        Pasien dewi = new Pasien("RM004", "Dewi Lestari", "3171046107880004", LocalDate.of(1988, 7, 21),
                Pasien.PEREMPUAN, "081288889999", "Jl. Ahmad Yani No. 21", JenisPasien.UMUM, "");
        pasienDao.tambah(budi);
        pasienDao.tambah(siti);
        pasienDao.tambah(andi);
        pasienDao.tambah(dewi);

        LocalDate hariIni = LocalDate.now();
        pemeriksaanDao.tambah(new PemeriksaanUmum("PR001", budi, rina, hariIni.minusDays(3),
                "Demam dan batuk", "ISPA", 30000, JenisPasien.BPJS));
        pemeriksaanDao.tambah(new PemeriksaanGigi("PR002", siti, maya, hariIni.minusDays(2),
                "Gigi berlubang", "Karies gigi", 25000, JenisPasien.UMUM, TindakanGigi.TAMBAL));
        pemeriksaanDao.tambah(new PemeriksaanGigi("PR003", andi, hendra, hariIni.minusDays(1),
                "Ingin gigi lebih putih", "Diskolorasi gigi", 0, JenisPasien.BPJS, TindakanGigi.BLEACHING));
        pemeriksaanDao.tambah(new PemeriksaanUmum("PR004", dewi, ahmad, hariIni,
                "Sakit kepala", "Tension headache", 35000, JenisPasien.UMUM));
    }
}
