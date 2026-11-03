package klinik.model;

import java.time.LocalDate;
import klinik.exception.ValidasiException;
import klinik.util.FormatUtil;

/**
 * Pemeriksaan di Poli Gigi (layanan khusus/spesialis, tarif lebih tinggi).
 * Aturan biaya:
 *   - Pasien BPJS : konsultasi & obat ditanggung; tindakan ditanggung KECUALI
 *                   tindakan estetika (bleaching) yang tetap dibayar penuh.
 *   - Pasien Umum : tarif konsultasi gigi + harga tindakan + biaya obat
 */
public class PemeriksaanGigi extends Pemeriksaan {

    public static final double TARIF_KONSULTASI = 100_000;

    private TindakanGigi tindakan;

    public PemeriksaanGigi(String id, Pasien pasien, Dokter dokter, LocalDate tanggal,
                           String keluhan, String diagnosa, double biayaObat, JenisPasien caraBayar,
                           TindakanGigi tindakan) throws ValidasiException {
        super(id, pasien, dokter, tanggal, keluhan, diagnosa, biayaObat, caraBayar);
        setTindakan(tindakan);
    }

    @Override
    public double hitungBiaya() {
        if (isBpjs()) {
            return tindakan.isDitanggungBpjs() ? 0 : tindakan.getHarga();
        }
        return TARIF_KONSULTASI + tindakan.getHarga() + getBiayaObat();
    }

    @Override
    public String getNamaLayanan() {
        return "Poli Gigi (Spesialis)";
    }

    @Override
    public Poli getPoliLayanan() {
        return Poli.GIGI;
    }

    @Override
    protected String rincianBiaya() {
        String ditanggung = isBpjs() ? " (ditanggung BPJS)" : "";
        String catatanTindakan = ditanggung;
        if (isBpjs() && !tindakan.isDitanggungBpjs()) {
            catatanTindakan = " (TIDAK ditanggung BPJS)";
        }
        return FormatUtil.baris("Konsultasi gigi", FormatUtil.rupiah(TARIF_KONSULTASI) + ditanggung)
                + FormatUtil.baris("Tindakan", tindakan + " " + FormatUtil.rupiah(tindakan.getHarga()) + catatanTindakan)
                + FormatUtil.baris("Obat", FormatUtil.rupiah(getBiayaObat()) + ditanggung);
    }

    public TindakanGigi getTindakan() {
        return tindakan;
    }

    public void setTindakan(TindakanGigi tindakan) throws ValidasiException {
        if (tindakan == null) {
            throw new ValidasiException("Pilih tindakan gigi.");
        }
        this.tindakan = tindakan;
    }
}
