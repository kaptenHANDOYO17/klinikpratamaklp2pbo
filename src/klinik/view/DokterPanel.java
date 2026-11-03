package klinik.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;
import klinik.controller.DokterController;
import klinik.exception.DataException;
import klinik.exception.ValidasiException;
import klinik.model.Dokter;
import klinik.model.Poli;

/** TAMPILAN: CRUD data dokter (Poli Umum & Poli Gigi). */
public class DokterPanel extends JPanel implements HalamanData {

    private static final long serialVersionUID = 1L;

    private final DokterController controller;
    private String idTerpilih = null;

    private final DefaultTableModel modelTabel = UiUtil.modelTabel(
            "ID", "Nama", "Poli", "No. SIP", "No. Telp", "Status");
    private final JTable tabel = UiUtil.buatTabel(modelTabel);
    private final JTextField txtCari = new JTextField(18);
    private final JComboBox<String> cbFilter = new JComboBox<>(new String[] {"Semua", "Poli Umum", "Poli Gigi"});
    private final JLabel lblJumlah = new JLabel();

    private final JTextField txtId = new JTextField(15);
    private final JTextField txtNama = new JTextField(15);
    private final JTextField txtTelp = new JTextField(15);
    private final JTextArea txtAlamat = new JTextArea(3, 15);
    private final JComboBox<Poli> cbPoli = new JComboBox<>(Poli.values());
    private final JTextField txtSip = new JTextField(15);
    private final JCheckBox chkAktif = new JCheckBox("Aktif praktik");

    private final Tombol btnTambah = new Tombol("Tambah", UiUtil.WARNA_SUKSES);
    private final Tombol btnUbah = new Tombol("Ubah", UiUtil.WARNA_UTAMA);
    private final Tombol btnHapus = new Tombol("Hapus", UiUtil.WARNA_BAHAYA);
    private final Tombol btnBersih = new Tombol("Bersihkan", UiUtil.WARNA_ABU);

    public DokterPanel(DokterController controller) {
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

        add(UiUtil.judulHalaman("Data Dokter", "Kelola dokter Poli Umum dan Poli Gigi (spesialis)"), BorderLayout.NORTH);
        add(UiUtil.splitFormTabel(buatForm(), buatBagianTabel()), BorderLayout.CENTER);
        UiUtil.aturLebarKolom(tabel, 60, 220, 90, 140, 120, 90);

        txtNama.setName("txtNama");
        txtTelp.setName("txtTelp");
        txtAlamat.setName("txtAlamat");
        cbPoli.setName("cbPoli");
        txtSip.setName("txtSip");
        chkAktif.setName("chkAktif");
        txtCari.setName("txtCari");
        cbFilter.setName("cbFilter");
        tabel.setName("tabel");
        btnTambah.setName("btnTambah");
        btnUbah.setName("btnUbah");
        btnHapus.setName("btnHapus");
        btnBersih.setName("btnBersih");
    }

    private JScrollPane buatForm() {
        JPanel form = UiUtil.panelForm("Form Dokter");
        txtId.setEditable(false);
        txtAlamat.setLineWrap(true);
        txtAlamat.setWrapStyleWord(true);
        chkAktif.setOpaque(false);

        int baris = 0;
        UiUtil.tambahField(form, baris++, "ID Dokter", txtId);
        UiUtil.tambahField(form, baris++, "Nama", txtNama);
        UiUtil.tambahField(form, baris++, "No. Telp", txtTelp);
        UiUtil.tambahField(form, baris++, "Alamat", new JScrollPane(txtAlamat));
        UiUtil.tambahField(form, baris++, "Poli", cbPoli);
        UiUtil.tambahField(form, baris++, "No. SIP", txtSip);
        UiUtil.tambahField(form, baris++, "Status", chkAktif);

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
        JPanel barCari = UiUtil.barCari(txtCari, "nama / ID / No. SIP", "Poli:", cbFilter);

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
        List<Dokter> daftar = controller.cari(txtCari.getText(), filterPoli());
        modelTabel.setRowCount(0);
        for (Dokter d : daftar) {
            modelTabel.addRow(new Object[] {
                d.getId(), d.getNama(), d.getPoli(), d.getNoSip(), d.getNoTelp(),
                d.isAktif() ? "Aktif" : "Tidak aktif"
            });
        }
        lblJumlah.setText("Jumlah data: " + daftar.size() + " dokter");
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
        Dokter d = controller.getById((String) modelTabel.getValueAt(baris, 0));
        if (d == null) {
            return;
        }
        idTerpilih = d.getId();
        txtId.setText(d.getId());
        txtNama.setText(d.getNama());
        txtTelp.setText(d.getNoTelp());
        txtAlamat.setText(d.getAlamat());
        cbPoli.setSelectedItem(d.getPoli());
        txtSip.setText(d.getNoSip());
        chkAktif.setSelected(d.isAktif());
    }

    private void tambahData() {
        try {
            Dokter d = controller.tambah(txtNama.getText(), txtTelp.getText(), txtAlamat.getText(),
                    (Poli) cbPoli.getSelectedItem(), txtSip.getText(), chkAktif.isSelected());
            UiUtil.pesanSukses(this, "Dokter " + d.getNama() + " berhasil ditambahkan dengan ID " + d.getId() + ".");
            muatUlang();
        } catch (ValidasiException e) {
            UiUtil.pesanError(this, e.getMessage());
        } catch (DataException e) {
            UiUtil.pesanErrorData(this, e.getMessage());
        }
    }

    private void ubahData() {
        try {
            Dokter d = controller.ubah(idTerpilih, txtNama.getText(), txtTelp.getText(), txtAlamat.getText(),
                    (Poli) cbPoli.getSelectedItem(), txtSip.getText(), chkAktif.isSelected());
            UiUtil.pesanSukses(this, "Data dokter " + d.getNama() + " berhasil diubah.");
            muatUlang();
        } catch (ValidasiException e) {
            UiUtil.pesanError(this, e.getMessage());
        } catch (DataException e) {
            UiUtil.pesanErrorData(this, e.getMessage());
        }
    }

    private void hapusData() {
        if (idTerpilih == null) {
            UiUtil.pesanError(this, "Pilih dokter di tabel terlebih dahulu.");
            return;
        }
        if (!UiUtil.konfirmasi(this, "Yakin ingin menghapus dokter " + txtNama.getText() + " (" + idTerpilih + ")?")) {
            return;
        }
        try {
            controller.hapus(idTerpilih);
            UiUtil.pesanSukses(this, "Data dokter berhasil dihapus.");
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
        txtTelp.setText("");
        txtAlamat.setText("");
        cbPoli.setSelectedItem(Poli.UMUM);
        txtSip.setText("");
        chkAktif.setSelected(true);
        txtNama.requestFocusInWindow();
    }
}
