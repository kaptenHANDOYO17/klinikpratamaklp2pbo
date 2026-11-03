package klinik.model;

/**
 * INTERFACE: kontrak untuk semua data yang disimpan aplikasi.
 * Diimplementasikan oleh lebih dari satu kelas:
 *   - Orang (lalu diwarisi Pasien, Dokter, Petugas)
 *   - Pemeriksaan (lalu diwarisi PemeriksaanUmum, PemeriksaanGigi)
 *
 * Berkat interface ini, DAO generik (FileDao) bisa menyimpan jenis data apa pun
 * selama data itu punya ID.
 */
public interface Entitas {

    /** ID unik data, contoh: RM001, DR001, PR001. */
    String getId();

    /** Teks informasi lengkap data ini (isinya berbeda untuk tiap kelas). */
    String tampilkanInfo();
}
