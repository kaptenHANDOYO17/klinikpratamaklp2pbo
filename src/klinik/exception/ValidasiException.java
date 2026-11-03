package klinik.exception;

/**
 * Custom exception (checked) untuk input yang tidak valid.
 * Contoh: nama kosong, NIK bukan 16 digit, biaya obat negatif.
 *
 * Karena extends Exception (bukan RuntimeException), setiap pemanggil
 * WAJIB menangkapnya dengan try-catch atau meneruskannya dengan "throws".
 */
public class ValidasiException extends Exception {

    private static final long serialVersionUID = 1L;

    public ValidasiException(String pesan) {
        super(pesan);
    }
}
