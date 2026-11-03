package klinik.controller;

import java.util.ArrayList;
import java.util.List;
import klinik.dao.DokterDao;
import klinik.dao.PemeriksaanDao;
import klinik.exception.DataException;
import klinik.exception.ValidasiException;
import klinik.model.Dokter;
import klinik.model.Poli;

/** Controller Dokter: penghubung DokterPanel dengan DokterDao. */
public class DokterController {

    private final DokterDao dokterDao;
    private final PemeriksaanDao pemeriksaanDao;

    public DokterController(DokterDao dokterDao, PemeriksaanDao pemeriksaanDao) {
        this.dokterDao = dokterDao;
        this.pemeriksaanDao = pemeriksaanDao;
    }

    public List<Dokter> getAll() {
        return dokterDao.getAll();
    }

    public Dokter getById(String id) {
        return dokterDao.cariById(id);
    }

    // ===== OVERLOADING =====

    public List<Dokter> cari(String kataKunci) {
        return cari(kataKunci, null);
    }

    public List<Dokter> cari(String kataKunci, Poli poli) {
        String kunci = (kataKunci == null) ? "" : kataKunci.trim().toLowerCase();
        List<Dokter> hasil = new ArrayList<>();
        for (Dokter d : dokterDao.getAll()) {
            boolean cocokKata = kunci.isEmpty()
                    || d.getId().toLowerCase().contains(kunci)
                    || d.getNama().toLowerCase().contains(kunci)
                    || d.getNoSip().toLowerCase().contains(kunci);
            boolean cocokPoli = (poli == null) || d.getPoli() == poli;
            if (cocokKata && cocokPoli) {
                hasil.add(d);
            }
        }
        return hasil;
    }

    /** Dokter yang sedang aktif praktik di poli tertentu (untuk pilihan di form pemeriksaan). */
    public List<Dokter> getDokterAktif(Poli poli) {
        List<Dokter> hasil = new ArrayList<>();
        for (Dokter d : dokterDao.getAll()) {
            if (d.isAktif() && d.getPoli() == poli) {
                hasil.add(d);
            }
        }
        return hasil;
    }

    // ===== CRUD =====

    public Dokter tambah(String nama, String noTelp, String alamat, Poli poli, String noSip, boolean aktif)
            throws ValidasiException, DataException {
        Dokter dokter = new Dokter(dokterDao.buatIdBaru(), nama, noTelp, alamat, poli, noSip, aktif);
        cekSipBelumDipakai(dokter.getNoSip(), null);
        dokterDao.tambah(dokter);
        return dokter;
    }

    public Dokter ubah(String id, String nama, String noTelp, String alamat, Poli poli, String noSip, boolean aktif)
            throws ValidasiException, DataException {
        Dokter lama = (id == null) ? null : dokterDao.cariById(id);
        if (lama == null) {
            throw new ValidasiException("Pilih dokter di tabel terlebih dahulu.");
        }
        Dokter dokter = new Dokter(id, nama, noTelp, alamat, poli, noSip, aktif);
        if (lama.getPoli() != dokter.getPoli() && pemeriksaanDao.adaUntukDokter(id)) {
            throw new ValidasiException("Poli tidak bisa diubah karena dokter ini sudah punya riwayat pemeriksaan.");
        }
        cekSipBelumDipakai(dokter.getNoSip(), id);
        dokterDao.ubah(dokter);
        pemeriksaanDao.muat(); // agar data pemeriksaan memakai data dokter yang terbaru
        return dokter;
    }

    public void hapus(String id) throws ValidasiException, DataException {
        if (id == null || dokterDao.cariById(id) == null) {
            throw new ValidasiException("Pilih dokter di tabel terlebih dahulu.");
        }
        if (pemeriksaanDao.adaUntukDokter(id)) {
            throw new ValidasiException("Dokter tidak bisa dihapus karena sudah punya riwayat pemeriksaan.\n"
                    + "Saran: hilangkan centang \"Aktif praktik\" agar tidak muncul lagi di pemeriksaan baru.");
        }
        dokterDao.hapus(id);
    }

    private void cekSipBelumDipakai(String noSip, String idDikecualikan) throws ValidasiException {
        for (Dokter d : dokterDao.getAll()) {
            if (d.getNoSip().equalsIgnoreCase(noSip) && !d.getId().equals(idDikecualikan)) {
                throw new ValidasiException("No. SIP " + noSip + " sudah dipakai oleh " + d.getNama() + ".");
            }
        }
    }
}
