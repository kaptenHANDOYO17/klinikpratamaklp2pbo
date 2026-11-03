package klinik.exception;

/**
 * Custom exception untuk kesalahan penyimpanan data (baca/tulis file),
 * misalnya file tidak bisa ditulis atau ID data tidak ditemukan.
 */
public class DataException extends Exception {

    private static final long serialVersionUID = 1L;

    public DataException(String pesan) {
        super(pesan);
    }

    public DataException(String pesan, Throwable penyebab) {
        super(pesan, penyebab);
    }
}
