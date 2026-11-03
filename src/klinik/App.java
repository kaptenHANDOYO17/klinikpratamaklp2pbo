package klinik;

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

    private void siapkanData() throws DataException, ValidasiException {
        // folder data bisa diganti lewat: java -Dklinik.data=folderLain ...
        String folderData = System.getProperty("klinik.data", "data");

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
