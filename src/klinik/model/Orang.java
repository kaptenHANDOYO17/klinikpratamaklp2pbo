package klinik.model;

import klinik.exception.ValidasiException;
import klinik.util.FormatUtil;

/**
 * ABSTRACT CLASS induk untuk Pasien, Dokter, dan Petugas.
 *
 * - Enkapsulasi : semua atribut private, diakses lewat getter/setter yang berisi validasi.
 * - Abstraksi   : tampilkanInfo() dan getPeran() abstrak, isinya ditentukan kelas anak.
 * - Pewarisan   : kelas anak memanggil konstruktor ini dengan super(...).
 */
public abstract class Orang implements Entitas {

    private String id;
    private String nama;
    private String noTelp;
    private String alamat;

    protected Orang(String id, String nama, String noTelp, String alamat) throws ValidasiException {
        setId(id);
        setNama(nama);
        setNoTelp(noTelp);
        setAlamat(alamat);
    }

    // ===== Method abstrak: WAJIB di-override oleh kelas anak =====

    /** Peran orang ini di klinik, contoh "Pasien". */
    public abstract String getPeran();

    /** Info lengkap; isinya berbeda untuk Pasien, Dokter, dan Petugas (polimorfisme). */
    @Override
    public abstract String tampilkanInfo();

    /** Bagian info yang sama untuk semua orang; dipakai kelas anak lewat super/infoDasar(). */
    protected String infoDasar() {
        return "=== " + getPeran().toUpperCase() + " ===\n"
                + FormatUtil.baris("ID", id)
                + FormatUtil.baris("Nama", nama)
                + FormatUtil.baris("No. Telp", noTelp)
                + FormatUtil.baris("Alamat", alamat);
    }

    // ===== Getter & Setter dengan validasi =====

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) throws ValidasiException {
        if (id == null || id.isBlank()) {
            throw new ValidasiException("ID tidak boleh kosong.");
        }
        this.id = id.trim();
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) throws ValidasiException {
        if (nama == null || nama.isBlank()) {
            throw new ValidasiException("Nama wajib diisi.");
        }
        String bersih = nama.trim();
        if (bersih.length() < 3) {
            throw new ValidasiException("Nama minimal 3 karakter.");
        }
        if (!bersih.matches("[A-Za-z .,'-]+")) {
            throw new ValidasiException("Nama hanya boleh berisi huruf, spasi, titik, koma, tanda petik, dan strip.");
        }
        this.nama = bersih;
    }

    public String getNoTelp() {
        return noTelp;
    }

    public void setNoTelp(String noTelp) throws ValidasiException {
        String bersih = (noTelp == null) ? "" : noTelp.trim();
        if (!bersih.matches("08\\d{8,11}")) {
            throw new ValidasiException("No. telepon harus diawali 08 dan berisi 10-13 digit angka.");
        }
        this.noTelp = bersih;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) throws ValidasiException {
        if (alamat == null || alamat.isBlank()) {
            throw new ValidasiException("Alamat wajib diisi.");
        }
        // alamat disimpan satu baris saja
        this.alamat = alamat.trim().replaceAll("\\s+", " ");
    }
}
