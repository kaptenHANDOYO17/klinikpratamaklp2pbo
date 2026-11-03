package klinik.dao;

import java.util.List;
import klinik.exception.DataException;
import klinik.model.Entitas;

/**
 * INTERFACE generik untuk operasi data (CRUD).
 * T adalah tipe data, misalnya Pasien atau Dokter (harus implement Entitas).
 */
public interface Dao<T extends Entitas> {

    List<T> getAll();

    T cariById(String id);

    void tambah(T data) throws DataException;

    void ubah(T data) throws DataException;

    void hapus(String id) throws DataException;

    String buatIdBaru();
}
