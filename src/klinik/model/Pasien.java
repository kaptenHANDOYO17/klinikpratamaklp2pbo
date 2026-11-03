package klinik.model;

import java.time.LocalDate;
import java.time.Period;
import klinik.exception.ValidasiException;
import klinik.util.FormatUtil;

/**
 * Pasien klinik. Turunan dari Orang.
 * Pasien bisa BPJS (wajib punya nomor BPJS 13 digit) atau Umum.
 */
public class Pasien extends Orang {

    public static final String LAKI_LAKI = "Laki-laki";
    public static final String PEREMPUAN = "Perempuan";

    private String nik;
    private LocalDate tanggalLahir;
    private String jenisKelamin;
    private JenisPasien jenisPasien;
    private String noBpjs;

    public Pasien(String id, String nama, String nik, LocalDate tanggalLahir, String jenisKelamin,
                  String noTelp, String alamat, JenisPasien jenisPasien, String noBpjs)
            throws ValidasiException {
        super(id, nama, noTelp, alamat);   // memanggil konstruktor kelas induk (Orang)
        setNik(nik);
        setTanggalLahir(tanggalLahir);
        setJenisKelamin(jenisKelamin);
        setJenisPasien(jenisPasien);       // harus sebelum setNoBpjs karena validasinya bergantung
        setNoBpjs(noBpjs);
    }

    @Override
    public String getPeran() {
        return "Pasien";
    }

    @Override
    public String tampilkanInfo() {
        String info = infoDasar()
                + FormatUtil.baris("NIK", nik)
                + FormatUtil.baris("Tanggal Lahir", FormatUtil.tanggal(tanggalLahir) + " (" + getUmur() + " tahun)")
                + FormatUtil.baris("Jenis Kelamin", jenisKelamin)
                + FormatUtil.baris("Jenis Pasien", jenisPasien.toString());
        if (isPasienBpjs()) {
            info += FormatUtil.baris("No. BPJS", noBpjs);
        }
        return info;
    }

    /** Dipakai JComboBox untuk menampilkan pasien. */
    @Override
    public String toString() {
        return getId() + " - " + getNama() + " (" + jenisPasien + ")";
    }

    public int getUmur() {
        return Period.between(tanggalLahir, LocalDate.now()).getYears();
    }

    public boolean isPasienBpjs() {
        return jenisPasien == JenisPasien.BPJS;
    }

    // ===== Getter & Setter dengan validasi =====

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) throws ValidasiException {
        String bersih = (nik == null) ? "" : nik.trim();
        if (!bersih.matches("\\d{16}")) {
            throw new ValidasiException("NIK harus 16 digit angka.");
        }
        this.nik = bersih;
    }

    public LocalDate getTanggalLahir() {
        return tanggalLahir;
    }

    public void setTanggalLahir(LocalDate tanggalLahir) throws ValidasiException {
        if (tanggalLahir == null) {
            throw new ValidasiException("Tanggal lahir wajib diisi.");
        }
        if (tanggalLahir.isAfter(LocalDate.now())) {
            throw new ValidasiException("Tanggal lahir tidak boleh melebihi hari ini.");
        }
        if (tanggalLahir.isBefore(LocalDate.now().minusYears(120))) {
            throw new ValidasiException("Tanggal lahir tidak masuk akal (lebih dari 120 tahun lalu).");
        }
        this.tanggalLahir = tanggalLahir;
    }

    public String getJenisKelamin() {
        return jenisKelamin;
    }

    public void setJenisKelamin(String jenisKelamin) throws ValidasiException {
        if (!LAKI_LAKI.equals(jenisKelamin) && !PEREMPUAN.equals(jenisKelamin)) {
            throw new ValidasiException("Pilih jenis kelamin (Laki-laki / Perempuan).");
        }
        this.jenisKelamin = jenisKelamin;
    }

    public JenisPasien getJenisPasien() {
        return jenisPasien;
    }

    public void setJenisPasien(JenisPasien jenisPasien) throws ValidasiException {
        if (jenisPasien == null) {
            throw new ValidasiException("Pilih jenis pasien (BPJS / Umum).");
        }
        this.jenisPasien = jenisPasien;
        if (jenisPasien == JenisPasien.UMUM) {
            this.noBpjs = "";  // pasien umum tidak memakai nomor BPJS
        }
    }

    public String getNoBpjs() {
        return noBpjs;
    }

    public void setNoBpjs(String noBpjs) throws ValidasiException {
        String bersih = (noBpjs == null) ? "" : noBpjs.trim();
        if (isPasienBpjs()) {
            if (!bersih.matches("\\d{13}")) {
                throw new ValidasiException("Pasien BPJS wajib mengisi No. BPJS (13 digit angka).");
            }
            this.noBpjs = bersih;
        } else {
            this.noBpjs = "";
        }
    }
}
