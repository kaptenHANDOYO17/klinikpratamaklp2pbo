package klinik;

import java.nio.file.Paths;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import klinik.controller.BerandaController;
import klinik.controller.DokterController;
import klinik.controller.LoginController;
import klinik.controller.PasienController;
import klinik.controller.PemeriksaanController;
import klinik.dao.DataAwal;
import klinik.dao.DokterDao;
import klinik.dao.PasienDao;
import klinik.dao.PemeriksaanDao;
import klinik.dao.PetugasDao;
import klinik.exception.DataException;
import klinik.exception.ValidasiException;
import klinik.model.Petugas;
import klinik.view.LoginFrame;
import klinik.view.MainFrame;
import klinik.view.UiUtil;

/**
 * TITIK AWAL APLIKASI (method main ada di sini).
 *
 * Urutan kerja:
 *   1. Siapkan DAO (baca file data) dan isi data contoh jika masih kosong.
 *   2. Buat semua controller.
 *   3. Tampilkan LoginFrame. Jika login berhasil, buka MainFrame.
 */
public class App {

    private LoginController loginController;
    private BerandaController berandaController;
    private PasienController pasienController;
    private DokterController dokterController;
    private PemeriksaanController pemeriksaanController;

    public static void main(String[] args) {
        // Semua kode Swing dijalankan di Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new App().mulai());
    }

    private void mulai() {
        UiUtil.aturTema();
        try {
            siapkanData();
        } catch (DataException | ValidasiException e) {
            JOptionPane.showMessageDialog(null, "Aplikasi gagal memuat data:\n" + e.getMessage(),
                    "Kesalahan", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        tampilkanLogin();
    }

    /**
     * Menentukan folder tempat file data disimpan:
     *  1. Jika diisi lewat -Dklinik.data=... , pakai folder itu.
     *  2. Jika dijalankan sebagai aplikasi TERPASANG (installer), pakai
     *     %APPDATA%\KlinikPratama\data, karena folder program tidak selalu boleh ditulisi.
     *  3. Selain itu (VS Code, terminal, file JAR), pakai folder "data" di tempat aplikasi dijalankan.
     */
    public static String tentukanFolderData() {
        String pilihan = System.getProperty("klinik.data");
        if (pilihan != null && !pilihan.isBlank()) {
            return Paths.get(pilihan).toAbsolutePath().toString();
        }
        if ("terpasang".equals(System.getProperty("klinik.mode"))) {
            String appData = System.getenv("APPDATA");
            String induk = (appData != null) ? appData : System.getProperty("user.home");
            return Paths.get(induk, "KlinikPratama", "data").toString();
        }
        return Paths.get("data").toAbsolutePath().toString();
    }

    private void siapkanData() throws DataException, ValidasiException {
        String folderData = tentukanFolderData();
        System.setProperty("klinik.data", folderData); // agar bisa ditampilkan di menu "Tentang"

        PetugasDao petugasDao = new PetugasDao(folderData);
        PasienDao pasienDao = new PasienDao(folderData);
        DokterDao dokterDao = new DokterDao(folderData);
        PemeriksaanDao pemeriksaanDao = new PemeriksaanDao(folderData, pasienDao, dokterDao);
        DataAwal.isiJikaKosong(petugasDao, dokterDao, pasienDao, pemeriksaanDao);

        loginController = new LoginController(petugasDao);
        berandaController = new BerandaController(pasienDao, dokterDao, petugasDao, pemeriksaanDao);
        pasienController = new PasienController(pasienDao, pemeriksaanDao);
        dokterController = new DokterController(dokterDao, pemeriksaanDao);
        pemeriksaanController = new PemeriksaanController(pemeriksaanDao, pasienDao, dokterDao);
    }

    private void tampilkanLogin() {
        new LoginFrame(loginController, this::tampilkanMenuUtama).setVisible(true);
    }

    private void tampilkanMenuUtama(Petugas petugas) {
        new MainFrame(petugas, berandaController, pasienController, dokterController,
                pemeriksaanController, this::tampilkanLogin).setVisible(true);
    }
}
