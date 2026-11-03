package klinik.dao;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import klinik.exception.DataException;
import klinik.exception.ValidasiException;
import klinik.model.Entitas;

/**
 * ABSTRACT CLASS generik yang menyimpan data ke file teks.
 * Satu baris file = satu objek, kolom dipisah tanda "|".
 * Contoh baris pasien:  RM001|Budi Santoso|3171...|1990-05-12|Laki-laki|...
 *
 * Data disimpan di memori dalam Map (ID -> objek) supaya pencarian by ID cepat,
 * lalu seluruh isi Map ditulis ulang ke file setiap kali ada perubahan.
 *
 * Kelas anak cukup menentukan cara mengubah objek <-> baris teks.
 */
public abstract class FileDao<T extends Entitas> implements Dao<T> {

    private static final String PEMISAH = "|";

    private final Path lokasiFile;
    private final String prefixId;
    private final Map<String, T> data = new LinkedHashMap<>();

    protected FileDao(String folderData, String namaFile, String prefixId) {
        this.lokasiFile = Paths.get(folderData, namaFile).toAbsolutePath();
        this.prefixId = prefixId;
    }

    // ===== Dua method yang wajib dibuat kelas anak =====

    /** Mengubah objek menjadi kolom-kolom teks. */
    protected abstract String[] keKolom(T objek);

    /** Mengubah kolom-kolom teks menjadi objek. */
    protected abstract T dariKolom(String[] kolom) throws ValidasiException;

    // ===== Baca & tulis file =====

    /**
     * Membaca seluruh isi file ke memori.
     * Dipanggil di akhir konstruktor kelas anak, dan bisa dipanggil ulang untuk refresh.
     */
    public void muat() throws DataException {
        data.clear();
        if (!Files.exists(lokasiFile)) {
            return; // belum ada file = belum ada data
        }
        try {
            List<String> semuaBaris = Files.readAllLines(lokasiFile, StandardCharsets.UTF_8);
            int nomorBaris = 0;
            for (String baris : semuaBaris) {
                nomorBaris++;
                if (baris.isBlank()) {
                    continue;
                }
                try {
                    T objek = dariKolom(baris.split("\\|", -1));
                    data.put(objek.getId(), objek);
                } catch (ValidasiException | RuntimeException e) {
                    // baris rusak dilewati, aplikasi tetap jalan
                    System.err.println("Peringatan: baris " + nomorBaris + " di "
                            + lokasiFile.getFileName() + " dilewati -> " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new DataException("Gagal membaca file " + lokasiFile.getFileName() + ": " + e.getMessage(), e);
        }
    }

    /** Menulis seluruh data di memori ke file. */
    private void simpanKeFile() throws DataException {
        List<String> semuaBaris = new ArrayList<>();
        for (T objek : data.values()) {
            String[] kolom = keKolom(objek);
            for (int i = 0; i < kolom.length; i++) {
                kolom[i] = bersihkan(kolom[i]);
            }
            semuaBaris.add(String.join(PEMISAH, kolom));
        }
        try {
            Files.createDirectories(lokasiFile.getParent());
            Files.write(lokasiFile, semuaBaris, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new DataException("Gagal menyimpan file " + lokasiFile.getFileName() + ": " + e.getMessage(), e);
        }
    }

    /** Karakter "|" dan enter tidak boleh masuk file karena akan merusak format. */
    private String bersihkan(String teks) {
        if (teks == null) {
            return "";
        }
        return teks.replace(PEMISAH, "/").replace("\r", " ").replace("\n", " ");
    }

    // ===== Implementasi interface Dao =====

    @Override
    public List<T> getAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public T cariById(String id) {
        return data.get(id);
    }

    @Override
    public void tambah(T objek) throws DataException {
        if (data.containsKey(objek.getId())) {
            throw new DataException("ID " + objek.getId() + " sudah dipakai.");
        }
        data.put(objek.getId(), objek);
        try {
            simpanKeFile();
        } catch (DataException e) {
            data.remove(objek.getId()); // batalkan perubahan di memori
            throw e;
        }
    }

    @Override
    public void ubah(T objek) throws DataException {
        if (!data.containsKey(objek.getId())) {
            throw new DataException("Data dengan ID " + objek.getId() + " tidak ditemukan.");
        }
        T lama = data.put(objek.getId(), objek);
        try {
            simpanKeFile();
        } catch (DataException e) {
            data.put(objek.getId(), lama); // kembalikan data lama
            throw e;
        }
    }

    @Override
    public void hapus(String id) throws DataException {
        if (!data.containsKey(id)) {
            throw new DataException("Data dengan ID " + id + " tidak ditemukan.");
        }
        T lama = data.remove(id);
        try {
            simpanKeFile();
        } catch (DataException e) {
            data.put(id, lama);
            throw e;
        }
    }

    /** Membuat ID berikutnya, contoh: data terakhir RM004 -> RM005. */
    @Override
    public String buatIdBaru() {
        int terbesar = 0;
        for (String id : data.keySet()) {
            if (id.startsWith(prefixId)) {
                try {
                    int angka = Integer.parseInt(id.substring(prefixId.length()));
                    terbesar = Math.max(terbesar, angka);
                } catch (NumberFormatException e) {
                    // ID dengan format lain diabaikan
                }
            }
        }
        return String.format("%s%03d", prefixId, terbesar + 1);
    }
}
