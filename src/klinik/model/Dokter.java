package klinik.model;

import klinik.exception.ValidasiException;
import klinik.util.FormatUtil;

/**
 * Dokter klinik. Turunan dari Orang.
 * Dokter bertugas di Poli Umum atau Poli Gigi, dan bisa aktif / tidak aktif praktik.
 */
public class Dokter extends Orang {

    private Poli poli;
    private String noSip;      // Surat Izin Praktik
    private boolean aktif;

    public Dokter(String id, String nama, String noTelp, String alamat,
                  Poli poli, String noSip, boolean aktif) throws ValidasiException {
        super(id, nama, noTelp, alamat);
        setPoli(poli);
        setNoSip(noSip);
        setAktif(aktif);
    }

    @Override
    public String getPeran() {
        return "Dokter";
    }

    @Override
    public String tampilkanInfo() {
        return infoDasar()
                + FormatUtil.baris("Poli", poli.toString())
                + FormatUtil.baris("No. SIP", noSip)
                + FormatUtil.baris("Status", aktif ? "Aktif praktik" : "Tidak aktif");
    }

    @Override
    public String toString() {
        return getId() + " - " + getNama();
    }

    // ===== Getter & Setter dengan validasi =====

    public Poli getPoli() {
        return poli;
    }

    public void setPoli(Poli poli) throws ValidasiException {
        if (poli == null) {
            throw new ValidasiException("Pilih poli dokter.");
        }
        this.poli = poli;
    }

    public String getNoSip() {
        return noSip;
    }

    public void setNoSip(String noSip) throws ValidasiException {
        String bersih = (noSip == null) ? "" : noSip.trim();
        if (bersih.length() < 5) {
            throw new ValidasiException("No. SIP wajib diisi (minimal 5 karakter).");
        }
        if (!bersih.matches("[A-Za-z0-9./-]+")) {
            throw new ValidasiException("No. SIP hanya boleh huruf, angka, titik, garis miring, dan strip.");
        }
        this.noSip = bersih;
    }

    public boolean isAktif() {
        return aktif;
    }

    public void setAktif(boolean aktif) {
        this.aktif = aktif;
    }
}
