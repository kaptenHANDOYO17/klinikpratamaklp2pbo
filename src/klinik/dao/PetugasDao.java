package klinik.dao;

import klinik.exception.DataException;
import klinik.exception.ValidasiException;
import klinik.model.Petugas;

/** Menyimpan akun petugas di file data/petugas.txt. ID: PT001, PT002, ... */
public class PetugasDao extends FileDao<Petugas> {

    public PetugasDao(String folderData) throws DataException {
        super(folderData, "petugas.txt", "PT");
        muat();
    }

    @Override
    protected String[] keKolom(Petugas p) {
        return new String[] {
            p.getId(), p.getNama(), p.getNoTelp(), p.getAlamat(),
            p.getJabatan(), p.getUsername(), p.getPassword()
        };
    }

    @Override
    protected Petugas dariKolom(String[] k) throws ValidasiException {
        return new Petugas(k[0], k[1], k[2], k[3], k[4], k[5], k[6]);
    }

    /** Mencari petugas berdasarkan username (null jika tidak ada). */
    public Petugas cariByUsername(String username) {
        for (Petugas p : getAll()) {
            if (p.getUsername().equals(username)) {
                return p;
            }
        }
        return null;
    }
}
