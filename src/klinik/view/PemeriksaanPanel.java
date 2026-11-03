package klinik.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;
import klinik.controller.DokterController;
import klinik.controller.PasienController;
import klinik.controller.PemeriksaanController;
import klinik.exception.DataException;
import klinik.exception.ValidasiException;
import klinik.model.Dokter;
import klinik.model.Entitas;
import klinik.model.Pasien;
import klinik.model.Pemeriksaan;
import klinik.model.PemeriksaanGigi;
import klinik.model.Poli;
import klinik.model.TindakanGigi;
import klinik.util.FormatUtil;

/** TAMPILAN: CRUD pemeriksaan pasien + hitung biaya + cetak struk. */
public class PemeriksaanPanel extends JPanel implements HalamanData {

    private static final long serialVersionUID = 1L;

    private final PemeriksaanController controller;
    private final PasienController pasienController;
    private final DokterController dokterController;
    private String idTerpilih = null;

    private final DefaultTableModel modelTabel = UiUtil.modelTabel(
            "ID", "Tanggal", "Pasien", "Cara Bayar", "Dokter", "Layanan", "Tindakan", "Total Biaya");
    private final JTable tabel = UiUtil.buatTabel(modelTabel);
    private final JTextField txtCari = new JTextField(16);
    private final JComboBox<String> cbFilter = new JComboBox<>(new String[] {"Semua", "Poli Umum", "Poli Gigi"});
    private final JLabel lblJumlah = new JLabel();

    private final JTextField txtId = new JTextField(15);
    private final JSpinner spTanggal = UiUtil.spinnerTanggal();
    private final JComboBox<Pasien> cbPasien = new JComboBox<>();
    private final JRadioButton rbUmum = new JRadioButton("Poli Umum");
    private final JRadioButton rbGigi = new JRadioButton("Poli Gigi");
    private final ButtonGroup grupLayanan = new ButtonGroup();
    private final JComboBox<Dokter> cbDokter = new JComboBox<>();
    private final JComboBox<TindakanGigi> cbTindakan = new JComboBox<>(TindakanGigi.values());
    private final JTextField txtKeluhan = new JTextField(15);
    private final JTextField txtDiagnosa = new JTextField(15);
    private final JSpinner spBiayaObat = UiUtil.spinnerRupiah(Pemeriksaan.BIAYA_OBAT_MAKS);
    private final JLabel lblBiaya = new JLabel();

    private final Tombol btnTambah = new Tombol("Tambah", UiUtil.WARNA_SUKSES);
    private final Tombol btnUbah = new Tombol("Ubah", UiUtil.WARNA_UTAMA);
    private final Tombol btnHapus = new Tombol("Hapus", UiUtil.WARNA_BAHAYA);
    private final Tombol btnBersih = new Tombol("Bersihkan", UiUtil.WARNA_ABU);
    private final Tombol btnHitung = new Tombol("Hitung Biaya", UiUtil.WARNA_INFO);
    private final Tombol btnStruk = new Tombol("Lihat Struk", UiUtil.WARNA_INFO);

    public PemeriksaanPanel(PemeriksaanController controller, PasienController pasienController,
                            DokterController dokterController) {
        this.controller = controller;
        this.pasienController = pasienController;
        this.dokterController = dokterController;
        susunTampilan();
        pasangEvent();
        muatUlang();
    }

    // ================= Susunan tampilan =================

    private void susunTampilan() {
        setLayout(new BorderLayout(12, 12));
        setBackground(UiUtil.WARNA_LATAR);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        add(UiUtil.judulHalaman("Pemeriksaan Pasien",
                "Daftarkan pemeriksaan, hitung biaya (Umum vs Spesialis, BPJS vs Umum), dan lihat struk"),
                BorderLayout.NORTH);
        add(UiUtil.splitFormTabel(buatForm(), buatBagianTabel()), BorderLayout.CENTER);
        UiUtil.aturLebarKolom(tabel, 60, 85, 130, 75, 160, 140, 150, 95);

        spTanggal.setName("spTanggal");
        cbPasien.setName("cbPasien");
        rbUmum.setName("rbUmum");
        rbGigi.setName("rbGigi");
        cbDokter.setName("cbDokter");
        cbTindakan.setName("cbTindakan");
        txtKeluhan.setName("txtKeluhan");
        txtDiagnosa.setName("txtDiagnosa");
        spBiayaObat.setName("spBiayaObat");
        lblBiaya.setName("lblBiaya");
        txtCari.setName("txtCari");
        cbFilter.setName("cbFilter");
        tabel.setName("tabel");
        btnTambah.setName("btnTambah");
        btnUbah.setName("btnUbah");
        btnHapus.setName("btnHapus");
        btnBersih.setName("btnBersih");
        btnHitung.setName("btnHitung");
        btnStruk.setName("btnStruk");
    }

    private JScrollPane buatForm() {
        JPanel form = UiUtil.panelForm("Form Pemeriksaan");
        txtId.setEditable(false);

        grupLayanan.add(rbUmum);
        grupLayanan.add(rbGigi);
        rbUmum.setOpaque(false);
        rbGigi.setOpaque(false);
        JPanel panelLayanan = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelLayanan.setOpaque(false);
        panelLayanan.add(rbUmum);
        panelLayanan.add(rbGigi);

        lblBiaya.setFont(lblBiaya.getFont().deriveFont(Font.BOLD, 14f));
        lblBiaya.setForeground(UiUtil.WARNA_GELAP);

        int baris = 0;
        UiUtil.tambahField(form, baris++, "No. Periksa", txtId);
        UiUtil.tambahField(form, baris++, "Tanggal", spTanggal);
        UiUtil.tambahField(form, baris++, "Pasien", cbPasien);
        UiUtil.tambahField(form, baris++, "Layanan", panelLayanan);
        UiUtil.tambahField(form, baris++, "Dokter", cbDokter);
        UiUtil.tambahField(form, baris++, "Tindakan Gigi", cbTindakan);
        UiUtil.tambahField(form, baris++, "Keluhan", txtKeluhan);
        UiUtil.tambahField(form, baris++, "Diagnosa", txtDiagnosa);
        UiUtil.tambahField(form, baris++, "Biaya Obat (Rp)", spBiayaObat);
        UiUtil.tambahField(form, baris++, "Biaya", lblBiaya);

        JPanel panelTombol = new JPanel(new GridLayout(3, 2, 8, 8));
        panelTombol.setOpaque(false);
        panelTombol.add(btnTambah);
        panelTombol.add(btnUbah);
        panelTombol.add(btnHapus);
        panelTombol.add(btnBersih);
        panelTombol.add(btnHitung);
        panelTombol.add(btnStruk);
        UiUtil.tambahPenuh(form, baris++, panelTombol);
        UiUtil.tambahPengisi(form, baris);

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setPreferredSize(new Dimension(400, 0));
        return scroll;
    }

    private JPanel buatBagianTabel() {
        JPanel barCari = UiUtil.barCari(txtCari, "nama pasien / dokter / diagnosa", "Layanan:", cbFilter);

        lblJumlah.setForeground(UiUtil.WARNA_ABU);

        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setOpaque(false);
        panel.add(barCari, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(tabel);
        scroll.getViewport().setBackground(Color.WHITE);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(lblJumlah, BorderLayout.SOUTH);
        return panel;
    }

    // ================= Event =================

    private void pasangEvent() {
        btnTambah.addActionListener(e -> tambahData());
        btnUbah.addActionListener(e -> ubahData());
        btnHapus.addActionListener(e -> hapusData());
        btnBersih.addActionListener(e -> bersihkanForm());
        btnHitung.addActionListener(e -> hitungBiaya());
        btnStruk.addActionListener(e -> lihatStruk());
        rbUmum.addActionListener(e -> gantiLayanan());
        rbGigi.addActionListener(e -> gantiLayanan());
        cbFilter.addActionListener(e -> muatTabel());
        UiUtil.saatTeksBerubah(txtCari, this::muatTabel);
        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                isiFormDariTabel();
            }
        });
    }

    /** Dipanggil saat halaman dibuka: pilihan pasien/dokter ikut diperbarui. */
    @Override
    public void muatUlang() {
        isiComboPasien();
        bersihkanForm();
        muatTabel();
    }

    private void muatTabel() {
        List<Pemeriksaan> daftar = controller.cari(txtCari.getText(), filterPoli());
        modelTabel.setRowCount(0);
        double total = 0;
        for (Pemeriksaan p : daftar) {
            String tindakan = "-";
            if (p instanceof PemeriksaanGigi) {
                tindakan = ((PemeriksaanGigi) p).getTindakan().toString();
            }
            modelTabel.addRow(new Object[] {
                p.getId(), FormatUtil.tanggal(p.getTanggal()), p.getPasien().getNama(), p.getCaraBayar(),
                p.getDokter().getNama(), p.getNamaLayanan(), tindakan, FormatUtil.rupiah(p.hitungBiaya())
            });
            total += p.hitungBiaya();
        }
        lblJumlah.setText("Jumlah data: " + daftar.size() + " pemeriksaan   |   Total biaya: " + FormatUtil.rupiah(total));
    }

    private Poli filterPoli() {
        int i = cbFilter.getSelectedIndex();
        return (i <= 0) ? null : Poli.values()[i - 1];
    }

    private void isiFormDariTabel() {
        int baris = tabel.getSelectedRow();
        if (baris < 0) {
            return;
        }
        Pemeriksaan p = controller.getById((String) modelTabel.getValueAt(baris, 0));
        if (p == null) {
            return;
        }
        idTerpilih = p.getId();
        txtId.setText(p.getId());
        UiUtil.aturTanggal(spTanggal, p.getTanggal());
        pilihDiCombo(cbPasien, p.getPasien().getId());

        boolean gigi = p.getPoliLayanan() == Poli.GIGI;
        rbGigi.setSelected(gigi);
        rbUmum.setSelected(!gigi);
        isiComboDokter(p.getPoliLayanan(), p.getDokter());
        pilihDiCombo(cbDokter, p.getDokter().getId());
        cbTindakan.setEnabled(gigi);
        if (p instanceof PemeriksaanGigi) {
            cbTindakan.setSelectedItem(((PemeriksaanGigi) p).getTindakan());
        }
        txtKeluhan.setText(p.getKeluhan());
        txtDiagnosa.setText("-".equals(p.getDiagnosa()) ? "" : p.getDiagnosa());
        spBiayaObat.setValue(p.getBiayaObat());
        lblBiaya.setText(FormatUtil.rupiah(p.hitungBiaya()) + " (" + p.getCaraBayar() + ")");
    }

    private void tambahData() {
        try {
            Pemeriksaan p = controller.tambah(idPasienTerpilih(), idDokterTerpilih(), UiUtil.ambilTanggal(spTanggal),
                    txtKeluhan.getText(), txtDiagnosa.getText(), UiUtil.ambilAngka(spBiayaObat), tindakanTerpilih());
            UiUtil.pesanSukses(this, "Pemeriksaan " + p.getId() + " berhasil disimpan.\nTotal biaya: "
                    + FormatUtil.rupiah(p.hitungBiaya()));
            muatUlangSetelahSimpan();
        } catch (ValidasiException e) {
            UiUtil.pesanError(this, e.getMessage());
        } catch (DataException e) {
            UiUtil.pesanErrorData(this, e.getMessage());
        }
    }

    private void ubahData() {
        try {
            Pemeriksaan p = controller.ubah(idTerpilih, idPasienTerpilih(), idDokterTerpilih(),
                    UiUtil.ambilTanggal(spTanggal), txtKeluhan.getText(), txtDiagnosa.getText(),
                    UiUtil.ambilAngka(spBiayaObat), tindakanTerpilih());
            UiUtil.pesanSukses(this, "Pemeriksaan " + p.getId() + " berhasil diubah.\nTotal biaya: "
                    + FormatUtil.rupiah(p.hitungBiaya()));
            muatUlangSetelahSimpan();
        } catch (ValidasiException e) {
            UiUtil.pesanError(this, e.getMessage());
        } catch (DataException e) {
            UiUtil.pesanErrorData(this, e.getMessage());
        }
    }

    private void hapusData() {
        if (idTerpilih == null) {
            UiUtil.pesanError(this, "Pilih data pemeriksaan di tabel terlebih dahulu.");
            return;
        }
        if (!UiUtil.konfirmasi(this, "Yakin ingin menghapus data pemeriksaan " + idTerpilih + "?")) {
            return;
        }
        try {
            controller.hapus(idTerpilih);
            UiUtil.pesanSukses(this, "Data pemeriksaan berhasil dihapus.");
            muatUlangSetelahSimpan();
        } catch (ValidasiException e) {
            UiUtil.pesanError(this, e.getMessage());
        } catch (DataException e) {
            UiUtil.pesanErrorData(this, e.getMessage());
        }
    }

    /** Menghitung biaya tanpa menyimpan, untuk memberi tahu pasien sebelum disimpan. */
    private void hitungBiaya() {
        try {
            double biaya = controller.hitungEstimasi(idPasienTerpilih(), idDokterTerpilih(),
                    UiUtil.ambilTanggal(spTanggal), txtKeluhan.getText(), txtDiagnosa.getText(),
                    UiUtil.ambilAngka(spBiayaObat), tindakanTerpilih());
            lblBiaya.setText(FormatUtil.rupiah(biaya) + " (estimasi)");
        } catch (ValidasiException e) {
            UiUtil.pesanError(this, e.getMessage());
        }
    }

    private void lihatStruk() {
        Pemeriksaan p = (idTerpilih == null) ? null : controller.getById(idTerpilih);
        if (p == null) {
            UiUtil.pesanError(this, "Pilih data pemeriksaan di tabel terlebih dahulu.");
            return;
        }
        UiUtil.tampilkanTeks(this, "Struk Pemeriksaan " + p.getId(), p.tampilkanInfo());
    }

    private void muatUlangSetelahSimpan() {
        bersihkanForm();
        muatTabel();
    }

    private void bersihkanForm() {
        idTerpilih = null;
        tabel.clearSelection();
        txtId.setText("(otomatis)");
        UiUtil.aturTanggal(spTanggal, LocalDate.now());
        if (cbPasien.getItemCount() > 0) {
            cbPasien.setSelectedIndex(0);
        }
        rbUmum.setSelected(true);
        gantiLayanan();
        cbTindakan.setSelectedIndex(0);
        txtKeluhan.setText("");
        txtDiagnosa.setText("");
        spBiayaObat.setValue(0.0);
    }

    /** Saat radio Poli Umum/Gigi diganti: daftar dokter disesuaikan, tindakan gigi aktif/nonaktif. */
    private void gantiLayanan() {
        Poli poli = rbGigi.isSelected() ? Poli.GIGI : Poli.UMUM;
        isiComboDokter(poli, null);
        cbTindakan.setEnabled(poli == Poli.GIGI);
        lblBiaya.setText("- (klik Hitung Biaya)");
    }

    // ================= Bantuan combo box =================

    private void isiComboPasien() {
        cbPasien.removeAllItems();
        for (Pasien p : pasienController.getAll()) {
            cbPasien.addItem(p);
        }
    }

    /**
     * Mengisi pilihan dokter yang aktif di poli tersebut.
     * dokterLama ikut dimasukkan (walau tidak aktif) agar data lama tetap bisa diedit.
     */
    private void isiComboDokter(Poli poli, Dokter dokterLama) {
        cbDokter.removeAllItems();
        for (Dokter d : dokterController.getDokterAktif(poli)) {
            cbDokter.addItem(d);
        }
        if (dokterLama != null && !pilihDiCombo(cbDokter, dokterLama.getId())) {
            cbDokter.addItem(dokterLama);
        }
    }

    /** Memilih item combo berdasarkan ID. Mengembalikan false jika tidak ditemukan. */
    private <T extends Entitas> boolean pilihDiCombo(JComboBox<T> combo, String id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).getId().equals(id)) {
                combo.setSelectedIndex(i);
                return true;
            }
        }
        return false;
    }

    private String idPasienTerpilih() {
        Pasien p = (Pasien) cbPasien.getSelectedItem();
        return (p == null) ? null : p.getId();
    }

    private String idDokterTerpilih() {
        Dokter d = (Dokter) cbDokter.getSelectedItem();
        return (d == null) ? null : d.getId();
    }

    private TindakanGigi tindakanTerpilih() {
        return rbGigi.isSelected() ? (TindakanGigi) cbTindakan.getSelectedItem() : null;
    }
}
