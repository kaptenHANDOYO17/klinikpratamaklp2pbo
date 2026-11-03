package klinik.controller;

import klinik.dao.PetugasDao;
import klinik.exception.ValidasiException;
import klinik.model.Petugas;

/** Controller untuk proses login petugas. */
public class LoginController {

    private final PetugasDao petugasDao;

    public LoginController(PetugasDao petugasDao) {
        this.petugasDao = petugasDao;
    }

    /**
     * Mengembalikan objek Petugas jika login berhasil.
     * Melempar ValidasiException jika input kosong atau username/password salah.
     */
    public Petugas login(String username, String password) throws ValidasiException {
        if (username == null || username.isBlank()) {
            throw new ValidasiException("Username wajib diisi.");
        }
        if (password == null || password.isEmpty()) {
            throw new ValidasiException("Password wajib diisi.");
        }
        Petugas petugas = petugasDao.cariByUsername(username.trim());
        if (petugas == null || !petugas.cocokLogin(username.trim(), password)) {
            // pesan sengaja tidak menyebut mana yang salah, demi keamanan
            throw new ValidasiException("Username atau password salah.");
        }
        return petugas;
    }
}
