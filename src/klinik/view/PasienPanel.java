package klinik.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;
import klinik.controller.PasienController;
import klinik.exception.DataException;
import klinik.exception.ValidasiException;
import klinik.model.JenisPasien;
import klinik.model.Pasien;
import klinik.util.FormatUtil;

/** TAMPILAN: CRUD data pasien (BPJS dan Umum). */
public class PasienPanel extends JPanel implements HalamanData {

    private static final long serialVersionUID = 1L;

    private final PasienController controller;
    private String idTerpilih = null;   // null = tidak ada baris yang dipilih

    // ----- komponen tabel & pencarian -----
    private final DefaultTableModel modelTabel = UiUtil.modelTabel(
            "No. RM", "Nama", "NIK", "Tgl Lahir", "Umur", "JK", "No. Telp", "Jenis", "No. BPJS");
    private final JTable tabel = UiUtil.buatTabel(modelTabel);
    private final JTextField txtCari = new JTextField(18);
    private final JComboBox<String> cbFilter = new JComboBox<>(new String[] {"Semua", "BPJS", "Umum"});
    private final JLabel lblJumlah = new JLabel();

    // ----- komponen form -----
    private final JTextField txtId = new JTextField(15);
    private final JTextField txtNama = new JTextField(15);
    private final JTextField txtNik = new JTextField(15);
    private final JSpinner spTanggalLahir = UiUtil.spinnerTanggal();
    private final JRadioButton rbLaki = new JRadioButton(Pasien.LAKI_LAKI);
    private final JRadioButton rbPerempuan = new JRadioButton(Pasien.PEREMPUAN);
    private final ButtonGroup grupJk = new ButtonGroup();
    private final JTextField txtTelp = new JTextField(15);
    private final JTextArea txtAlamat = new JTextArea(3, 15);
    private final JComboBox<JenisPasien> cbJenis = new JComboBox<>(JenisPasien.values());
    private final JTextField txtBpjs = new JTextField(15);

    private final Tombol btnTambah = new Tombol("Tambah", UiUtil.WARNA_SUKSES);
    private final Tombol btnUbah = new Tombol("Ubah", UiUtil.WARNA_UTAMA);
    private final Tombol btnHapus = new Tombol("Hapus", UiUtil.WARNA_BAHAYA);
    private final Tombol btnBersih = new Tombol("Bersihkan", UiUtil.WARNA_ABU);

    public PasienPanel(PasienController controller) {
        this.controller = controller;
        susunTampilan();
        pasangEvent();
        muatUlang();
    }

    // ================= Susunan tampilan =================

    private void susunTampilan() {
        setLayout(new BorderLayout(12, 12));
        setBackground(UiUtil.WARNA_LATAR);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        add(UiUtil.judulHalaman("Data Pasien", "Kelola data pasien BPJS dan Umum"), BorderLayout.NORTH);
        add(UiUtil.splitFormTabel(buatForm(), buatBagianTabel()), BorderLayout.CENTER);
        UiUtil.aturLebarKolom(tabel, 90, 150, 140, 90, 55, 85, 110, 60, 120);

        txtNama.setName("txtNama");
        txtNik.setName("txtNik");
        txtTelp.setName("txtTelp");
        txtAlamat.setName("txtAlamat");
        txtBpjs.setName("txtBpjs");
        cbJenis.setName("cbJenis");
        rbLaki.setName("rbLaki");
        rbPerempuan.setName("rbPerempuan");
        spTanggalLahir.setName("spTanggal");
        txtCari.setName("txtCari");
        cbFilter.setName("cbFilter");
        tabel.setName("tabel");
        btnTambah.setName("btnTambah");
        btnUbah.setName("btnUbah");
        btnHapus.setName("btnHapus");
        btnBersih.setName("btnBersih");
    }

    private JScrollPane buatForm() {
        JPanel form = UiUtil.panelForm("Form Pasien");
        txtId.setEditable(false);
        txtAlamat.setLineWrap(true);
        txtAlamat.setWrapStyleWord(true);

        grupJk.add(rbLaki);
        grupJk.add(rbPerempuan);
        JPanel panelJk = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelJk.setOpaque(false);
        rbLaki.setOpaque(false);
        rbPerempuan.setOpaque(false);
        panelJk.add(rbLaki);
        panelJk.add(rbPerempuan);

        int baris = 0;
        UiUtil.tambahField(form, baris++, "No. RM", txtId);
        UiUtil.tambahField(form, baris++, "Nama", txtNama);
        UiUtil.tambahField(form, baris++, "NIK (16 digit)", txtNik);
        UiUtil.tambahField(form, baris++, "Tanggal Lahir", spTanggalLahir);
        UiUtil.tambahField(form, baris++, "Jenis Kelamin", panelJk);
        UiUtil.tambahField(form, baris++, "No. Telp", txtTelp);
        UiUtil.tambahField(form, baris++, "Alamat", new JScrollPane(txtAlamat));
        UiUtil.tambahField(form, baris++, "Jenis Pasien", cbJenis);
        UiUtil.tambahField(form, baris++, "No. BPJS (13 digit)", txtBpjs);

        JPanel panelTombol = new JPanel(new GridLayout(2, 2, 8, 8));
        panelTombol.setOpaque(false);
        panelTombol.add(btnTambah);
        panelTombol.add(btnUbah);
        panelTombol.add(btnHapus);
        panelTombol.add(btnBersih);
        UiUtil.tambahPenuh(form, baris++, panelTombol);
        UiUtil.tambahPengisi(form, baris);

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setPreferredSize(new Dimension(380, 0));
        return scroll;
    }

    private JPanel buatBagianTabel() {
        JPanel barCari = UiUtil.barCari(txtCari, "nama / No. RM / NIK / No. BPJS", "Jenis:", cbFilter);

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

    // ================= Event (setiap aksi punya method sendiri) =================

    private void pasangEvent() {
        btnTambah.addActionListener(e -> tambahData());
        btnUbah.addActionListener(e -> ubahData());
        btnHapus.addActionListener(e -> hapusData());
        btnBersih.addActionListener(e -> bersihkanForm());
        cbJenis.addActionListener(e -> aturFieldBpjs());
        cbFilter.addActionListener(e -> muatTabel());
        UiUtil.saatTeksBerubah(txtCari, this::muatTabel);
        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                isiFormDariTabel();
            }
        });
    }

    @Override
    public void muatUlang() {
        bersihkanForm();
        muatTabel();
    }

    private void muatTabel() {
        List<Pasien> daftar = controller.cari(txtCari.getText(), filterJenis());
        modelTabel.setRowCount(0);
        for (Pasien p : daftar) {
            modelTabel.addRow(new Object[] {
                p.getId(), p.getNama(), p.getNik(), FormatUtil.tanggal(p.getTanggalLahir()),
                p.getUmur() + " th", p.getJenisKelamin(), p.getNoTelp(), p.getJenisPasien(),
                p.isPasienBpjs() ? p.getNoBpjs() : "-"
            });
        }
        lblJumlah.setText("Jumlah data: " + daftar.size() + " pasien");
    }

    /** Index 0 = "Semua" (null), 1 = BPJS, 2 = Umum. */
    private JenisPasien filterJenis() {
        int i = cbFilter.getSelectedIndex();
        return (i <= 0) ? null : JenisPasien.values()[i - 1];
    }

    private void isiFormDariTabel() {
        int baris = tabel.getSelectedRow();
        if (baris < 0) {
            return;
        }
        Pasien p = controller.getById((String) modelTabel.getValueAt(baris, 0));
        if (p == null) {
            return;
        }
        idTerpilih = p.getId();
        txtId.setText(p.getId());
        txtNama.setText(p.getNama());
        txtNik.setText(p.getNik());
        UiUtil.aturTanggal(spTanggalLahir, p.getTanggalLahir());
        rbLaki.setSelected(Pasien.LAKI_LAKI.equals(p.getJenisKelamin()));
        rbPerempuan.setSelected(Pasien.PEREMPUAN.equals(p.getJenisKelamin()));
        txtTelp.setText(p.getNoTelp());
        txtAlamat.setText(p.getAlamat());
        cbJenis.setSelectedItem(p.getJenisPasien());
        txtBpjs.setText(p.getNoBpjs());
    }

    private void tambahData() {
        try {
            Pasien p = controller.tambah(txtNama.getText(), txtNik.getText(), UiUtil.ambilTanggal(spTanggalLahir),
                    jenisKelaminTerpilih(), txtTelp.getText(), txtAlamat.getText(),
                    (JenisPasien) cbJenis.getSelectedItem(), txtBpjs.getText());
            UiUtil.pesanSukses(this, "Pasien " + p.getNama() + " berhasil ditambahkan.\nNo. RM: " + p.getId());
            muatUlang();
        } catch (ValidasiException e) {
            UiUtil.pesanError(this, e.getMessage());
        } catch (DataException e) {
            UiUtil.pesanErrorData(this, e.getMessage());
        }
    }

    private void ubahData() {
        try {
            Pasien p = controller.ubah(idTerpilih, txtNama.getText(), txtNik.getText(),
                    UiUtil.ambilTanggal(spTanggalLahir), jenisKelaminTerpilih(), txtTelp.getText(),
                    txtAlamat.getText(), (JenisPasien) cbJenis.getSelectedItem(), txtBpjs.getText());
            UiUtil.pesanSukses(this, "Data pasien " + p.getNama() + " berhasil diubah.");
            muatUlang();
        } catch (ValidasiException e) {
            UiUtil.pesanError(this, e.getMessage());
        } catch (DataException e) {
            UiUtil.pesanErrorData(this, e.getMessage());
        }
    }

    private void hapusData() {
        if (idTerpilih == null) {
            UiUtil.pesanError(this, "Pilih pasien di tabel terlebih dahulu.");
            return;
        }
        if (!UiUtil.konfirmasi(this, "Yakin ingin menghapus pasien " + txtNama.getText() + " (" + idTerpilih + ")?")) {
            return;
        }
        try {
            controller.hapus(idTerpilih);
            UiUtil.pesanSukses(this, "Data pasien berhasil dihapus.");
            muatUlang();
        } catch (ValidasiException e) {
            UiUtil.pesanError(this, e.getMessage());
        } catch (DataException e) {
            UiUtil.pesanErrorData(this, e.getMessage());
        }
    }

    private void bersihkanForm() {
        idTerpilih = null;
        tabel.clearSelection();
        txtId.setText("(otomatis)");
        txtNama.setText("");
        txtNik.setText("");
        UiUtil.aturTanggal(spTanggalLahir, LocalDate.of(2000, 1, 1));
        grupJk.clearSelection();
        txtTelp.setText("");
        txtAlamat.setText("");
        cbJenis.setSelectedItem(JenisPasien.BPJS);
        txtBpjs.setText("");
        aturFieldBpjs();
        txtNama.requestFocusInWindow();
    }

    /** No. BPJS hanya bisa diisi jika jenis pasien = BPJS. */
    private void aturFieldBpjs() {
        boolean bpjs = cbJenis.getSelectedItem() == JenisPasien.BPJS;
        txtBpjs.setEnabled(bpjs);
        if (!bpjs) {
            txtBpjs.setText("");
        }
    }

    private String jenisKelaminTerpilih() {
        if (rbLaki.isSelected()) {
            return Pasien.LAKI_LAKI;
        }
        if (rbPerempuan.isSelected()) {
            return Pasien.PEREMPUAN;
        }
        return null;
    }
}
