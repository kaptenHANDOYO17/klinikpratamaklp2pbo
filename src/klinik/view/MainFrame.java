package klinik.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import klinik.controller.BerandaController;
import klinik.controller.DokterController;
import klinik.controller.PasienController;
import klinik.controller.PemeriksaanController;
import klinik.model.Petugas;
import klinik.util.FormatUtil;

/**
 * TAMPILAN UTAMA setelah login.
 * Navigasi konsisten lewat MENU BAR (atas) dan SIDEBAR (kiri).
 * Isi tengah memakai CardLayout: hanya satu halaman yang tampil dalam satu waktu.
 */
public class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final String BERANDA = "Beranda";
    private static final String PASIEN = "Pasien";
    private static final String DOKTER = "Dokter";
    private static final String PEMERIKSAAN = "Pemeriksaan";

    private final Petugas petugas;
    private final BerandaController berandaController;
    private final Runnable saatLogout;

    private final CardLayout kartu = new CardLayout();
    private final JPanel panelIsi = new JPanel(kartu);
    private final Map<String, HalamanData> halaman = new LinkedHashMap<>();
    private final Map<String, Tombol> tombolNav = new LinkedHashMap<>();
    private final JLabel lblHalaman = new JLabel();

    public MainFrame(Petugas petugas, BerandaController berandaController, PasienController pasienController,
                     DokterController dokterController, PemeriksaanController pemeriksaanController,
                     Runnable saatLogout) {
        super("Klinik Pratama - Sistem Informasi Pelayanan Pasien");
        this.petugas = petugas;
        this.berandaController = berandaController;
        this.saatLogout = saatLogout;

        tambahHalaman(BERANDA, new BerandaPanel(berandaController, petugas));
        tambahHalaman(PASIEN, new PasienPanel(pasienController));
        tambahHalaman(DOKTER, new DokterPanel(dokterController));
        tambahHalaman(PEMERIKSAAN, new PemeriksaanPanel(pemeriksaanController, pasienController, dokterController));

        susunTampilan();
        tampilkan(BERANDA);
    }

    private <T extends JComponent & HalamanData> void tambahHalaman(String nama, T panel) {
        panel.setName("halaman" + nama);
        halaman.put(nama, panel);
        panelIsi.add(panel, nama);
    }

    private void susunTampilan() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                keluar();
            }
        });
        setSize(1200, 720);
        setMinimumSize(new Dimension(1000, 600));
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // langsung layar penuh

        setJMenuBar(buatMenuBar());
        JPanel isi = new JPanel(new BorderLayout());
        isi.add(buatSidebar(), BorderLayout.WEST);
        isi.add(panelIsi, BorderLayout.CENTER);
        isi.add(buatStatusBar(), BorderLayout.SOUTH);
        setContentPane(isi);
    }

    // ================= Menu bar =================

    private JMenuBar buatMenuBar() {
        JMenuBar bar = new JMenuBar();

        JMenu menuFile = new JMenu("File");
        menuFile.add(itemMenu("Beranda", KeyEvent.VK_1, () -> tampilkan(BERANDA)));
        menuFile.addSeparator();
        menuFile.add(itemMenu("Logout", KeyEvent.VK_L, this::logout));
        menuFile.add(itemMenu("Keluar", KeyEvent.VK_Q, this::keluar));

        JMenu menuData = new JMenu("Data Master");
        menuData.add(itemMenu("Data Pasien", KeyEvent.VK_2, () -> tampilkan(PASIEN)));
        menuData.add(itemMenu("Data Dokter", KeyEvent.VK_3, () -> tampilkan(DOKTER)));

        JMenu menuTransaksi = new JMenu("Transaksi");
        menuTransaksi.add(itemMenu("Pemeriksaan Pasien", KeyEvent.VK_4, () -> tampilkan(PEMERIKSAAN)));

        JMenu menuBantuan = new JMenu("Bantuan");
        menuBantuan.add(itemMenu("Info Tarif Layanan", 0, this::tampilkanInfoTarif));
        menuBantuan.add(itemMenu("Tentang Aplikasi", 0, this::tampilkanTentang));

        bar.add(menuFile);
        bar.add(menuData);
        bar.add(menuTransaksi);
        bar.add(menuBantuan);
        return bar;
    }

    /** Membuat item menu dengan shortcut Ctrl+tombol (kodeTombol 0 = tanpa shortcut). */
    private JMenuItem itemMenu(String teks, int kodeTombol, Runnable aksi) {
        JMenuItem item = new JMenuItem(teks);
        if (kodeTombol != 0) {
            item.setAccelerator(KeyStroke.getKeyStroke(kodeTombol, InputEvent.CTRL_DOWN_MASK));
        }
        item.addActionListener(e -> aksi.run());
        return item;
    }

    // ================= Sidebar =================

    private JPanel buatSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UiUtil.WARNA_GELAP);
        sidebar.setBorder(BorderFactory.createEmptyBorder(18, 12, 18, 12));
        sidebar.setPreferredSize(new Dimension(200, 0));

        JLabel logo = new JLabel("KLINIK PRATAMA");
        logo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 17));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(LEFT_ALIGNMENT);
        JLabel subLogo = new JLabel("Pelayanan Pasien");
        subLogo.setForeground(new Color(180, 220, 220));
        subLogo.setAlignmentX(LEFT_ALIGNMENT);
        sidebar.add(logo);
        sidebar.add(subLogo);
        sidebar.add(Box.createVerticalStrut(24));

        sidebar.add(tombolSidebar(BERANDA, "Beranda", () -> tampilkan(BERANDA)));
        sidebar.add(tombolSidebar(PASIEN, "Data Pasien", () -> tampilkan(PASIEN)));
        sidebar.add(tombolSidebar(DOKTER, "Data Dokter", () -> tampilkan(DOKTER)));
        sidebar.add(tombolSidebar(PEMERIKSAAN, "Pemeriksaan", () -> tampilkan(PEMERIKSAAN)));
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(tombolSidebar(null, "Logout", this::logout));
        return sidebar;
    }

    private JComponent tombolSidebar(String kunci, String teks, Runnable aksi) {
        Tombol tombol = new Tombol(teks, UiUtil.WARNA_GELAP);
        tombol.setHorizontalAlignment(SwingConstants.LEFT);
        tombol.setAlignmentX(LEFT_ALIGNMENT);
        tombol.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        tombol.setName("nav" + teks.replace(" ", ""));
        tombol.addActionListener(e -> aksi.run());
        if (kunci != null) {
            tombolNav.put(kunci, tombol);
        }
        JPanel bungkus = new JPanel(new BorderLayout());
        bungkus.setOpaque(false);
        bungkus.setAlignmentX(LEFT_ALIGNMENT);
        bungkus.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        bungkus.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
        bungkus.add(tombol);
        return bungkus;
    }

    private JPanel buatStatusBar() {
        JPanel status = new JPanel(new BorderLayout());
        status.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        status.setBackground(new Color(225, 238, 238));
        JLabel lblLogin = new JLabel("Login sebagai: " + petugas.getNama() + " (" + petugas.getJabatan() + ")");
        JLabel lblTanggal = new JLabel("Tanggal: " + FormatUtil.tanggal(LocalDate.now()));
        lblHalaman.setForeground(UiUtil.WARNA_ABU);
        lblHalaman.setHorizontalAlignment(SwingConstants.CENTER);
        status.add(lblLogin, BorderLayout.WEST);
        status.add(lblHalaman, BorderLayout.CENTER);
        status.add(lblTanggal, BorderLayout.EAST);
        return status;
    }

    // ================= Aksi navigasi =================

    /** Menampilkan halaman tertentu dan memuat ulang datanya. */
    public void tampilkan(String nama) {
        halaman.get(nama).muatUlang();
        kartu.show(panelIsi, nama);
        for (Map.Entry<String, Tombol> entri : tombolNav.entrySet()) {
            boolean aktif = entri.getKey().equals(nama);
            entri.getValue().setWarna(aktif ? UiUtil.WARNA_UTAMA : UiUtil.WARNA_GELAP);
        }
        lblHalaman.setText("Halaman: " + nama);
    }

    private void tampilkanInfoTarif() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-46s %-14s %s%n", "LAYANAN", "PASIEN UMUM", "PASIEN BPJS"));
        sb.append("-".repeat(96)).append("\n");
        for (String[] baris : berandaController.getDaftarTarif()) {
            sb.append(String.format("%-46s %-14s %s", baris[0], baris[1], baris[2])).append("\n");
        }
        sb.append("\nCatatan: Poli Gigi adalah layanan khusus (spesialis) sehingga tarif konsultasinya lebih tinggi.\n");
        sb.append("Bleaching adalah tindakan estetika, tidak ditanggung BPJS.");
        UiUtil.tampilkanTeks(this, "Info Tarif Layanan", sb.toString().replace("\r", ""));
    }

    private void tampilkanTentang() {
        UiUtil.tampilkanTeks(this, "Tentang Aplikasi",
                "KLINIK PRATAMA - Sistem Informasi Pelayanan Pasien\n\n"
                + "Tugas Proyek PBO: Implementasi GUI (2026)\n"
                + "Bahasa  : Java (Swing)\n"
                + "Pola    : MVC (Model - View - Controller) + DAO\n"
                + "Data    : file teks di folder 'data'\n\n"
                + "Fitur   : login petugas, CRUD pasien (BPJS/Umum),\n"
                + "          CRUD dokter (Poli Umum/Gigi), CRUD pemeriksaan,\n"
                + "          hitung biaya otomatis & struk pemeriksaan.");
    }

    private void logout() {
        if (UiUtil.konfirmasi(this, "Yakin ingin logout?")) {
            dispose();
            saatLogout.run();
        }
    }

    private void keluar() {
        if (UiUtil.konfirmasi(this, "Yakin ingin keluar dari aplikasi?")) {
            System.exit(0);
        }
    }
}
