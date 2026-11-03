package klinik.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JSplitPane;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import klinik.exception.ValidasiException;

/**
 * Kumpulan fungsi bantu tampilan agar kode di setiap panel lebih pendek dan seragam:
 * warna tema, pesan dialog, pembuatan tabel, form, dan spinner.
 */
public final class UiUtil {

    // ===== Warna tema klinik =====
    public static final Color WARNA_UTAMA = new Color(13, 110, 110);     // hijau toska
    public static final Color WARNA_GELAP = new Color(9, 78, 78);
    public static final Color WARNA_LATAR = new Color(242, 247, 247);
    public static final Color WARNA_SUKSES = new Color(22, 128, 61);
    public static final Color WARNA_BAHAYA = new Color(190, 40, 40);
    public static final Color WARNA_ABU = new Color(100, 116, 139);
    public static final Color WARNA_INFO = new Color(37, 99, 235);

    private UiUtil() {
    }

    /** Mengatur tampilan (Look and Feel) Nimbus dengan warna klinik. */
    public static void aturTema() {
        try {
            UIManager.put("nimbusBase", WARNA_GELAP);
            UIManager.put("nimbusSelectionBackground", WARNA_UTAMA);
            UIManager.put("nimbusFocus", new Color(94, 180, 180));
            UIManager.put("control", WARNA_LATAR);
            UIManager.put("defaultFont", new Font(Font.SANS_SERIF, Font.PLAIN, 13));
            UIManager.put("Table.alternateRowColor", new Color(236, 245, 245));
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // jika gagal, Java tetap memakai tampilan bawaan; aplikasi tetap berjalan
            System.err.println("Tema Nimbus tidak tersedia: " + e.getMessage());
        }
    }

    /** Ikon jendela aplikasi (kotak toska dengan tanda +), digambar dengan kode tanpa file gambar. */
    public static java.awt.Image ikonAplikasi() {
        int u = 64;
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(u, u, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g = img.createGraphics();
        g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(WARNA_UTAMA);
        g.fillRoundRect(0, 0, u, u, 16, 16);
        g.setColor(Color.WHITE);
        g.fillRect(26, 12, 12, 40);
        g.fillRect(12, 26, 40, 12);
        g.dispose();
        return img;
    }

    // ===== Dialog pesan =====

    public static void pesanError(Component induk, String pesan) {
        JOptionPane.showMessageDialog(induk, pesan, "Input Tidak Valid", JOptionPane.ERROR_MESSAGE);
    }

    public static void pesanErrorData(Component induk, String pesan) {
        JOptionPane.showMessageDialog(induk, pesan, "Gagal Menyimpan Data", JOptionPane.ERROR_MESSAGE);
    }

    public static void pesanSukses(Component induk, String pesan) {
        JOptionPane.showMessageDialog(induk, pesan, "Berhasil", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Dialog Ya/Tidak. Mengembalikan true jika pengguna memilih Ya. */
    public static boolean konfirmasi(Component induk, String pesan) {
        int pilihan = JOptionPane.showConfirmDialog(induk, pesan, "Konfirmasi",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        return pilihan == JOptionPane.YES_OPTION;
    }

    /** Menampilkan teks panjang (misal struk) di dialog dengan huruf monospace. */
    public static void tampilkanTeks(Component induk, String judul, String teks) {
        JTextArea area = new JTextArea(teks);
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        area.setCaretPosition(0);
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new java.awt.Dimension(780, 420));
        JOptionPane.showMessageDialog(induk, scroll, judul, JOptionPane.INFORMATION_MESSAGE);
    }

    // ===== Tabel =====

    /** Model tabel yang sel-selnya tidak bisa diedit langsung. */
    public static DefaultTableModel modelTabel(String... namaKolom) {
        return new DefaultTableModel(namaKolom, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int baris, int kolom) {
                return false;
            }
        };
    }

    public static JTable buatTabel(DefaultTableModel model) {
        JTable tabel = new JTable(model) {
            private static final long serialVersionUID = 1L;

            /** Menampilkan isi sel lengkap saat kursor diarahkan (berguna jika teks terpotong). */
            @Override
            public String getToolTipText(java.awt.event.MouseEvent e) {
                int baris = rowAtPoint(e.getPoint());
                int kolom = columnAtPoint(e.getPoint());
                if (baris < 0 || kolom < 0) {
                    return null;
                }
                return String.valueOf(getValueAt(baris, kolom));
            }
        };
        javax.swing.ToolTipManager.sharedInstance().registerComponent(tabel);
        tabel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabel.setRowHeight(26);
        tabel.setFillsViewportHeight(true);
        tabel.getTableHeader().setReorderingAllowed(false);
        tabel.getTableHeader().setFont(tabel.getTableHeader().getFont().deriveFont(Font.BOLD));
        return tabel;
    }

    /** Mengatur lebar awal tiap kolom (sebagai perbandingan saat tabel melebar/menyempit). */
    public static void aturLebarKolom(JTable tabel, int... lebar) {
        for (int i = 0; i < lebar.length && i < tabel.getColumnCount(); i++) {
            tabel.getColumnModel().getColumn(i).setPreferredWidth(lebar[i]);
        }
    }

    /** Bar pencarian: "Cari: [.........]   Filter: [combo]" + petunjuk kecil di bawahnya. */
    public static JPanel barCari(JTextField txtCari, String petunjuk, String labelFilter, JComboBox<String> cbFilter) {
        JPanel bar = new JPanel(new BorderLayout(6, 2));
        bar.setOpaque(false);
        bar.add(new JLabel("Cari:"), BorderLayout.WEST);
        txtCari.setToolTipText("Cari berdasarkan " + petunjuk);
        bar.add(txtCari, BorderLayout.CENTER);

        JPanel kanan = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 6, 0));
        kanan.setOpaque(false);
        kanan.add(new JLabel(labelFilter));
        kanan.add(cbFilter);
        bar.add(kanan, BorderLayout.EAST);

        JLabel lblPetunjuk = new JLabel("Ketik " + petunjuk + " - hasil langsung tersaring");
        lblPetunjuk.setForeground(WARNA_ABU);
        lblPetunjuk.setFont(lblPetunjuk.getFont().deriveFont(11f));
        bar.add(lblPetunjuk, BorderLayout.SOUTH);
        return bar;
    }

    /** Form di kiri dan tabel di kanan, dipisah garis yang bisa digeser (JSplitPane). */
    public static JSplitPane splitFormTabel(JComponent form, JComponent tabel) {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, form, tabel);
        split.setBorder(null);
        split.setOpaque(false);
        split.setResizeWeight(0);        // saat jendela melebar, yang ikut melebar adalah tabel
        split.setContinuousLayout(true);
        split.setOneTouchExpandable(true);
        split.setDividerSize(10);
        return split;
    }

    // ===== Form =====

    /** Panel form dengan GridBagLayout dan judul berbingkai. */
    public static JPanel panelForm(String judul) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        TitledBorder bingkai = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 220, 220)), judul);
        bingkai.setTitleFont(bingkai.getTitleFont() == null
                ? new Font(Font.SANS_SERIF, Font.BOLD, 13)
                : bingkai.getTitleFont().deriveFont(Font.BOLD, 13f));
        bingkai.setTitleColor(WARNA_GELAP);
        panel.setBorder(BorderFactory.createCompoundBorder(bingkai, BorderFactory.createEmptyBorder(6, 8, 8, 8)));
        return panel;
    }

    /** Menambah satu baris "Label : komponen" ke panel form. */
    public static void tambahField(JPanel form, int baris, String label, JComponent komponen) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = baris;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.insets = new Insets(5, 2, 5, 10);
        form.add(new JLabel(label), c);

        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(3, 0, 3, 2);
        form.add(komponen, c);
    }

    /** Menambah komponen selebar 2 kolom (misal deretan tombol). */
    public static void tambahPenuh(JPanel form, int baris, JComponent komponen) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = baris;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 0, 3, 0);
        form.add(komponen, c);
    }

    /** Ruang kosong di bawah form agar isi form tetap rapat di atas saat jendela diperbesar. */
    public static void tambahPengisi(JPanel form, int baris) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = baris;
        c.weighty = 1;
        form.add(new JLabel(), c);
    }

    /** Judul halaman + keterangan kecil di bawahnya. */
    public static JPanel judulHalaman(String judul, String keterangan) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JLabel lblJudul = new JLabel(judul);
        lblJudul.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        lblJudul.setForeground(WARNA_GELAP);
        JLabel lblKet = new JLabel(keterangan);
        lblKet.setForeground(WARNA_ABU);
        panel.add(lblJudul, BorderLayout.NORTH);
        panel.add(lblKet, BorderLayout.SOUTH);
        return panel;
    }

    // ===== Event =====

    /** Menjalankan aksi setiap kali isi text field berubah (dipakai untuk pencarian langsung). */
    public static void saatTeksBerubah(JTextField field, Runnable aksi) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                aksi.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                aksi.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                aksi.run();
            }
        });
    }

    // ===== Spinner tanggal & angka =====

    /** Spinner tanggal (pengganti date picker) dengan format dd-MM-yyyy. */
    public static JSpinner spinnerTanggal() {
        JSpinner spinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        spinner.setEditor(new JSpinner.DateEditor(spinner, "dd-MM-yyyy"));
        return spinner;
    }

    public static LocalDate ambilTanggal(JSpinner spinner) throws ValidasiException {
        try {
            spinner.commitEdit();  // pastikan teks yang diketik ikut terbaca
        } catch (ParseException e) {
            throw new ValidasiException("Format tanggal salah. Gunakan format dd-MM-yyyy.");
        }
        Date tanggal = (Date) spinner.getValue();
        return tanggal.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static void aturTanggal(JSpinner spinner, LocalDate tanggal) {
        spinner.setValue(Date.from(tanggal.atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    /** Spinner angka rupiah, kelipatan 5.000. */
    public static JSpinner spinnerRupiah(double maksimal) {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, maksimal, 5000.0));
        spinner.setEditor(new JSpinner.NumberEditor(spinner, "#,##0"));
        return spinner;
    }

    public static double ambilAngka(JSpinner spinner) throws ValidasiException {
        try {
            spinner.commitEdit();
        } catch (ParseException e) {
            throw new ValidasiException("Angka tidak valid. Isi hanya dengan angka.");
        }
        return ((Number) spinner.getValue()).doubleValue();
    }
}
