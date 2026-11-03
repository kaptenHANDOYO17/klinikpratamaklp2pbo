package klinik.dao;

import klinik.exception.DataException;
import klinik.exception.ValidasiException;
import klinik.model.Dokter;
import klinik.model.Poli;

/** Menyimpan data dokter di file data/dokter.txt. ID: DR001, DR002, ... */
public class DokterDao extends FileDao<Dokter> {

    public DokterDao(String folderData) throws DataException {
        super(folderData, "dokter.txt", "DR");
        muat();
    }

    @Override
    protected String[] keKolom(Dokter d) {
        return new String[] {
            d.getId(), d.getNama(), d.getNoTelp(), d.getAlamat(),
            d.getPoli().name(), d.getNoSip(), String.valueOf(d.isAktif())
        };
    }

    @Override
    protected Dokter dariKolom(String[] k) throws ValidasiException {
        return new Dokter(k[0], k[1], k[2], k[3], Poli.valueOf(k[4]), k[5], Boolean.parseBoolean(k[6]));
    }
}
