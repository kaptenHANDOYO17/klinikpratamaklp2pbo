package klinik.model;

import java.time.LocalDate;
import klinik.exception.ValidasiException;
import klinik.util.FormatUtil;

/**
 * Pemeriksaan di Poli Umum.
 * Aturan biaya:
 *   - Pasien BPJS : Rp 0 (konsultasi dan obat ditanggung BPJS)
 *   - Pasien Umum : tarif konsultasi umum + biaya obat
 */
public class PemeriksaanUmum extends Pemeriksaan {

    public static final double TARIF_KONSULTASI = 50_000;

    public PemeriksaanUmum(String id, Pasien pasien, Dokter dokter, LocalDate tanggal,
                           String keluhan, String diagnosa, double biayaObat, JenisPasien caraBayar)
            throws ValidasiException {
        super(id, pasien, dokter, tanggal, keluhan, diagnosa, biayaObat, caraBayar);
    }

    @Override
    public double hitungBiaya() {
        if (isBpjs()) {
            return 0;
        }
        return TARIF_KONSULTASI + getBiayaObat();
    }

    @Override
    public String getNamaLayanan() {
        return "Poli Umum";
    }

    @Override
    public Poli getPoliLayanan() {
        return Poli.UMUM;
    }

    @Override
    protected String rincianBiaya() {
        String catatan = isBpjs() ? " (ditanggung BPJS)" : "";
        return FormatUtil.baris("Konsultasi umum", FormatUtil.rupiah(TARIF_KONSULTASI) + catatan)
                + FormatUtil.baris("Obat", FormatUtil.rupiah(getBiayaObat()) + catatan);
    }
}
