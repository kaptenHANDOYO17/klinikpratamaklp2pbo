package klinik.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import klinik.controller.BerandaController;
import klinik.model.JenisPasien;
import klinik.model.Petugas;
import klinik.model.Poli;
import klinik.util.FormatUtil;

/** TAMPILAN: beranda/menu utama berisi ringkasan data dan tabel tarif layanan. */
public class BerandaPanel extends JPanel implements HalamanData {

    private static final long serialVersionUID = 1L;

    private final BerandaController controller;

    private final JLabel lblPasienBpjs = new JLabel();
    private final JLabel lblPasienUmum = new JLabel();
    private final JLabel lblDokterUmum = new JLabel();
    private final JLabel lblDokterGigi = new JLabel();
    private final JLabel lblPemeriksaan = new JLabel();
    private final JLabel lblPendapatan = new JLabel();
    private final DefaultTableModel modelTarif = UiUtil.modelTabel("Layanan", "Pasien Umum", "Pasien BPJS");
    private final Tombol btnInfoOrang = new Tombol("Tampilkan Info Semua Orang", UiUtil.WARNA_UTAMA);

    public BerandaPanel(BerandaController controller, Petugas petugas) {
        this.controller = controller;
        susunTampilan(petugas);
        btnInfoOrang.addActionListener(e -> tampilkanInfoSemuaOrang());
        muatUlang();
    }

    private void susunTampilan(Petugas petugas) {
        setLayout(new BorderLayout(12, 12));
        setBackground(UiUtil.WARNA_LATAR);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        add(UiUtil.judulHalaman("Selamat datang, " + petugas.getNama(),
                petugas.getJabatan() + "  |  Ringkasan pelayanan Klinik Pratama"), BorderLayout.NORTH);

        JPanel kartu = new JPanel(new GridLayout(2, 3, 12, 12));
        kartu.setOpaque(false);
        kartu.add(buatKartu("Pasien BPJS", lblPasienBpjs, UiUtil.WARNA_SUKSES));
        kartu.add(buatKartu("Pasien Umum", lblPasienUmum, UiUtil.WARNA_INFO));
        kartu.add(buatKartu("Total Pemeriksaan", lblPemeriksaan, UiUtil.WARNA_ABU));
        kartu.add(buatKartu("Dokter Poli Umum", lblDokterUmum, UiUtil.WARNA_UTAMA));
        kartu.add(buatKartu("Dokter Poli Gigi", lblDokterGigi, UiUtil.WARNA_UTAMA));
        kartu.add(buatKartu("Total Pendapatan", lblPendapatan, UiUtil.WARNA_BAHAYA));

        JTable tabelTarif = UiUtil.buatTabel(modelTarif);
        tabelTarif.setRowSelectionAllowed(false);
        JScrollPane scrollTarif = new JScrollPane(tabelTarif);
        scrollTarif.getViewport().setBackground(Color.WHITE);

        JLabel judulTarif = new JLabel("Tarif Layanan: Poli Umum vs Poli Gigi (Spesialis)");
        judulTarif.setFont(judulTarif.getFont().deriveFont(Font.BOLD, 15f));
        judulTarif.setForeground(UiUtil.WARNA_GELAP);

        JPanel bagianTarif = new JPanel(new BorderLayout(6, 6));
        bagianTarif.setOpaque(false);
        bagianTarif.add(judulTarif, BorderLayout.NORTH);
        bagianTarif.add(scrollTarif, BorderLayout.CENTER);

        JPanel tengah = new JPanel(new BorderLayout(12, 16));
        tengah.setOpaque(false);
        tengah.add(kartu, BorderLayout.NORTH);
        tengah.add(bagianTarif, BorderLayout.CENTER);
        add(tengah, BorderLayout.CENTER);

        JPanel bawah = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bawah.setOpaque(false);
        btnInfoOrang.setName("btnInfoOrang");
        bawah.add(btnInfoOrang);
        add(bawah, BorderLayout.SOUTH);
    }

    private JPanel buatKartu(String judul, JLabel lblNilai, Color warna) {
        JPanel kartu = new JPanel(new BorderLayout(4, 4));
        kartu.setBackground(Color.WHITE);
        kartu.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, warna),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        JLabel lblJudul = new JLabel(judul);
        lblJudul.setForeground(UiUtil.WARNA_ABU);
        lblNilai.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        lblNilai.setForeground(warna);
        kartu.add(lblJudul, BorderLayout.NORTH);
        kartu.add(lblNilai, BorderLayout.CENTER);
        return kartu;
    }

    @Override
    public void muatUlang() {
        lblPasienBpjs.setText(String.valueOf(controller.jumlahPasien(JenisPasien.BPJS)));
        lblPasienUmum.setText(String.valueOf(controller.jumlahPasien(JenisPasien.UMUM)));
        lblDokterUmum.setText(String.valueOf(controller.jumlahDokter(Poli.UMUM)));
        lblDokterGigi.setText(String.valueOf(controller.jumlahDokter(Poli.GIGI)));
        lblPemeriksaan.setText(String.valueOf(controller.jumlahPemeriksaan()));
        lblPendapatan.setText(FormatUtil.rupiah(controller.totalPendapatan()));

        modelTarif.setRowCount(0);
        for (String[] baris : controller.getDaftarTarif()) {
            modelTarif.addRow(baris);
        }
    }

    /** Demonstrasi polimorfisme: tampilkanInfo() milik Pasien, Dokter, dan Petugas berbeda-beda. */
    private void tampilkanInfoSemuaOrang() {
        UiUtil.tampilkanTeks(this, "Info Semua Orang (Polimorfisme tampilkanInfo)", controller.getInfoSemuaOrang());
    }
}
