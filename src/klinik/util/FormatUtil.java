package klinik.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Kumpulan fungsi bantu untuk format tampilan (rupiah, tanggal, baris info).
 * Semua method static karena tidak butuh objek.
 */
public final class FormatUtil {

    private static final Locale INDONESIA = Locale.forLanguageTag("id-ID");
    private static final DateTimeFormatter FORMAT_TANGGAL = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private FormatUtil() {
        // tidak boleh dibuat objeknya
    }

    /** Contoh: 50000 -> "Rp 50.000" */
    public static String rupiah(double nilai) {
        return String.format(INDONESIA, "Rp %,.0f", nilai);
    }

    /** Contoh: 2026-11-03 -> "03-11-2026" */
    public static String tanggal(LocalDate tanggal) {
        return tanggal == null ? "-" : tanggal.format(FORMAT_TANGGAL);
    }

    /** Membuat satu baris info yang rata, contoh: "Nama          : Budi" */
    public static String baris(String label, String nilai) {
        return String.format("%-20s: %s", label, nilai) + "\n";
    }
}
