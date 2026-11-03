package klinik.view;

/**
 * Interface untuk halaman di menu utama.
 * Setiap kali halaman dibuka, MainFrame memanggil muatUlang()
 * agar data yang tampil selalu yang terbaru.
 */
public interface HalamanData {

    void muatUlang();
}
