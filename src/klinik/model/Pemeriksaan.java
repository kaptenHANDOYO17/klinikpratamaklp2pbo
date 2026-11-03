package klinik.model;

import java.time.LocalDate;
import klinik.exception.ValidasiException;
import klinik.util.FormatUtil;

/**
 * ABSTRACT CLASS untuk satu kunjungan/pemeriksaan pasien.
 * Kelas anak: PemeriksaanUmum dan PemeriksaanGigi.
 *
 * POLIMORFISME: hitungBiaya() dipanggil lewat referensi Pemeriksaan,
 * tetapi hasilnya berbeda tergantung objeknya Umum atau Gigi.
 *
 * Catatan desain: "caraBayar" disimpan terpisah dari jenis pasien.
 * Tujuannya agar biaya kunjungan lama TIDAK ikut berubah bila suatu saat
 * pasien pindah dari Umum ke BPJS (atau sebaliknya).
 */
public abstract class Pemeriksaan implements Entitas {

    public static final double BIAYA_OBAT_MAKS = 10_000_000;

    private String id;
    private Pasien pasien;
    private Dokter dokter;
    private LocalDate tanggal;
    private String keluhan;
    private String diagnosa;
    private double biayaObat;
    private JenisPasien caraBayar;

    protected Pemeriksaan(String id, Pasien pasien, Dokter dokter, LocalDate tanggal,
                          String keluhan, String diagnosa, double biayaObat, JenisPasien caraBayar)
            throws ValidasiException {
        setId(id);
        setPasien(pasien);
        setDokter(dokter);
        setTanggal(tanggal);
        setKeluhan(keluhan);
        setDiagnosa(diagnosa);
        setBiayaObat(biayaObat);
        setCaraBayar(caraBayar);
    }

    // ===== Method abstrak: isinya berbeda di tiap kelas anak =====

    /** Total biaya yang harus dibayar pasien. */
    public abstract double hitungBiaya();

    /** Nama layanan, contoh "Poli Umum". */
    public abstract String getNamaLayanan();

    /** Poli yang melayani pemeriksaan ini (dipakai untuk validasi dokter). */
    public abstract Poli getPoliLayanan();

    /** Rincian biaya per item untuk struk. */
    protected abstract String rincianBiaya();

    /** Struk pemeriksaan. Memanggil method abstrak di atas (polimorfisme). */
    @Override
    public String tampilkanInfo() {
        String garis = "------------------------------------------------\n";
        return "============= STRUK PEMERIKSAAN =============\n"
                + FormatUtil.baris("No. Pemeriksaan", id)
                + FormatUtil.baris("Tanggal", FormatUtil.tanggal(tanggal))
                + FormatUtil.baris("Pasien", pasien.getNama() + " (" + pasien.getId() + ")")
                + FormatUtil.baris("Cara Bayar", caraBayar.toString())
                + FormatUtil.baris("Layanan", getNamaLayanan())
                + FormatUtil.baris("Dokter", dokter.getNama())
                + FormatUtil.baris("Keluhan", keluhan)
                + FormatUtil.baris("Diagnosa", diagnosa)
                + garis
                + rincianBiaya()
                + garis
                + FormatUtil.baris("TOTAL BAYAR", FormatUtil.rupiah(hitungBiaya()));
    }

    public boolean isBpjs() {
        return caraBayar == JenisPasien.BPJS;
    }

    // ===== Getter & Setter dengan validasi =====

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) throws ValidasiException {
        if (id == null || id.isBlank()) {
            throw new ValidasiException("ID pemeriksaan tidak boleh kosong.");
        }
        this.id = id.trim();
    }

    public Pasien getPasien() {
        return pasien;
    }

    public void setPasien(Pasien pasien) throws ValidasiException {
        if (pasien == null) {
            throw new ValidasiException("Pilih pasien terlebih dahulu.");
        }
        this.pasien = pasien;
    }

    public Dokter getDokter() {
        return dokter;
    }

    public void setDokter(Dokter dokter) throws ValidasiException {
        if (dokter == null) {
            throw new ValidasiException("Pilih dokter terlebih dahulu.");
        }
        if (dokter.getPoli() != getPoliLayanan()) {
            throw new ValidasiException(dokter.getNama() + " bukan dokter " + getPoliLayanan() + ".");
        }
        this.dokter = dokter;
    }

    public LocalDate getTanggal() {
        return tanggal;
    }

    public void setTanggal(LocalDate tanggal) throws ValidasiException {
        if (tanggal == null) {
            throw new ValidasiException("Tanggal pemeriksaan wajib diisi.");
        }
        if (tanggal.isAfter(LocalDate.now())) {
            throw new ValidasiException("Tanggal pemeriksaan tidak boleh melebihi hari ini.");
        }
        if (pasien != null && tanggal.isBefore(pasien.getTanggalLahir())) {
            throw new ValidasiException("Tanggal pemeriksaan tidak boleh sebelum tanggal lahir pasien.");
        }
        this.tanggal = tanggal;
    }

    public String getKeluhan() {
        return keluhan;
    }

    public void setKeluhan(String keluhan) throws ValidasiException {
        if (keluhan == null || keluhan.trim().length() < 3) {
            throw new ValidasiException("Keluhan wajib diisi (minimal 3 karakter).");
        }
        this.keluhan = keluhan.trim();
    }

    public String getDiagnosa() {
        return diagnosa;
    }

    /** Diagnosa boleh kosong (misalnya belum diperiksa), disimpan sebagai "-". */
    public void setDiagnosa(String diagnosa) {
        this.diagnosa = (diagnosa == null || diagnosa.isBlank()) ? "-" : diagnosa.trim();
    }

    public double getBiayaObat() {
        return biayaObat;
    }

    public void setBiayaObat(double biayaObat) throws ValidasiException {
        if (biayaObat < 0) {
            throw new ValidasiException("Biaya obat tidak boleh negatif.");
        }
        if (biayaObat > BIAYA_OBAT_MAKS) {
            throw new ValidasiException("Biaya obat maksimal " + FormatUtil.rupiah(BIAYA_OBAT_MAKS) + ".");
        }
        this.biayaObat = biayaObat;
    }

    public JenisPasien getCaraBayar() {
        return caraBayar;
    }

    public void setCaraBayar(JenisPasien caraBayar) throws ValidasiException {
        if (caraBayar == null) {
            throw new ValidasiException("Cara bayar wajib diisi.");
        }
        this.caraBayar = caraBayar;
    }
}
