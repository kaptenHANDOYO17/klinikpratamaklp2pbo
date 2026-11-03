package klinik.model;

import klinik.exception.ValidasiException;
import klinik.util.FormatUtil;

/**
 * Petugas klinik (admin pendaftaran / kasir). Turunan dari Orang.
 * Petugas memiliki akun untuk login ke aplikasi.
 */
public class Petugas extends Orang {

    private String jabatan;
    private String username;
    private String password;

    public Petugas(String id, String nama, String noTelp, String alamat,
                   String jabatan, String username, String password) throws ValidasiException {
        super(id, nama, noTelp, alamat);
        setJabatan(jabatan);
        setUsername(username);
        setPassword(password);
    }

    @Override
    public String getPeran() {
        return "Petugas";
    }

    @Override
    public String tampilkanInfo() {
        // password sengaja TIDAK ditampilkan
        return infoDasar()
                + FormatUtil.baris("Jabatan", jabatan)
                + FormatUtil.baris("Username", username);
    }

    /** Mengecek apakah username & password cocok dengan akun petugas ini. */
    public boolean cocokLogin(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    // ===== Getter & Setter dengan validasi =====

    public String getJabatan() {
        return jabatan;
    }

    public void setJabatan(String jabatan) throws ValidasiException {
        if (jabatan == null || jabatan.isBlank()) {
            throw new ValidasiException("Jabatan wajib diisi.");
        }
        this.jabatan = jabatan.trim();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) throws ValidasiException {
        String bersih = (username == null) ? "" : username.trim();
        if (!bersih.matches("[A-Za-z0-9_]{4,20}")) {
            throw new ValidasiException("Username 4-20 karakter (huruf, angka, garis bawah).");
        }
        this.username = bersih;
    }

    /** Hanya dipakai DAO untuk menyimpan ke file. */
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) throws ValidasiException {
        if (password == null || password.length() < 6) {
            throw new ValidasiException("Password minimal 6 karakter.");
        }
        this.password = password;
    }
}
