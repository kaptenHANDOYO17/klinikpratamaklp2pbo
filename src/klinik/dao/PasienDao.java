package klinik.dao;

import java.time.LocalDate;
import klinik.exception.DataException;
import klinik.exception.ValidasiException;
import klinik.model.JenisPasien;
import klinik.model.Pasien;

/** Menyimpan data pasien di file data/pasien.txt. ID: RM001, RM002, ... */
public class PasienDao extends FileDao<Pasien> {

    public PasienDao(String folderData) throws DataException {
        super(folderData, "pasien.txt", "RM");
        muat();
    }

    @Override
    protected String[] keKolom(Pasien p) {
        return new String[] {
            p.getId(), p.getNama(), p.getNik(), p.getTanggalLahir().toString(),
            p.getJenisKelamin(), p.getNoTelp(), p.getAlamat(),
            p.getJenisPasien().name(), p.getNoBpjs()
        };
    }

    @Override
    protected Pasien dariKolom(String[] k) throws ValidasiException {
        return new Pasien(k[0], k[1], k[2], LocalDate.parse(k[3]), k[4], k[5], k[6],
                JenisPasien.valueOf(k[7]), k[8]);
    }
}
