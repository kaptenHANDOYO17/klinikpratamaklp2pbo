package klinik.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import klinik.controller.LoginController;
import klinik.exception.ValidasiException;
import klinik.model.Petugas;

/** TAMPILAN 1: halaman login petugas. */
public class LoginFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private final LoginController controller;
    private final Consumer<Petugas> saatBerhasil;   // apa yang dilakukan setelah login sukses

    private final JTextField txtUsername = new JTextField(18);
    private final JPasswordField txtPassword = new JPasswordField(18);
    private final Tombol btnLogin = new Tombol("Masuk", UiUtil.WARNA_UTAMA);

    public LoginFrame(LoginController controller, Consumer<Petugas> saatBerhasil) {
        super("Login - Klinik Pratama");
        this.controller = controller;
        this.saatBerhasil = saatBerhasil;
        susunTampilan();
        pasangEvent();
    }

    private void susunTampilan() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(760, 440);
        setMinimumSize(new Dimension(640, 400));
        setLocationRelativeTo(null);

        JPanel isi = new JPanel(new GridLayout(1, 2));
        isi.add(buatPanelKiri());
        isi.add(buatPanelForm());
        setContentPane(isi);
        getRootPane().setDefaultButton(btnLogin); // tekan Enter = klik Masuk
        txtUsername.setName("txtUsername");
        txtPassword.setName("txtPassword");
        btnLogin.setName("btnLogin");
    }

    private JPanel buatPanelKiri() {
        JPanel kiri = new JPanel(new GridBagLayout());
        kiri.setBackground(UiUtil.WARNA_UTAMA);

        JLabel ikon = new JLabel("+", SwingConstants.CENTER);
        ikon.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 64));
        ikon.setForeground(Color.WHITE);
        JLabel nama = new JLabel("KLINIK PRATAMA", SwingConstants.CENTER);
        nama.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 26));
        nama.setForeground(Color.WHITE);
        JLabel sub = new JLabel("Sistem Informasi Pelayanan Pasien", SwingConstants.CENTER);
        sub.setForeground(new Color(210, 240, 240));
        JLabel sub2 = new JLabel("Poli Umum  |  Poli Gigi  |  BPJS & Umum", SwingConstants.CENTER);
        sub2.setForeground(new Color(210, 240, 240));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.insets = new Insets(4, 10, 4, 10);
        kiri.add(ikon, c);
        kiri.add(nama, c);
        kiri.add(sub, c);
        kiri.add(sub2, c);
        return kiri;
    }

    private JPanel buatPanelForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel judul = new JLabel("Login Petugas");
        judul.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        judul.setForeground(UiUtil.WARNA_GELAP);
        JLabel petunjuk = new JLabel("Akun awal: admin / admin123");
        petunjuk.setForeground(UiUtil.WARNA_ABU);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(4, 0, 4, 0);
        form.add(judul, c);
        form.add(petunjuk, c);
        c.insets = new Insets(14, 0, 2, 0);
        form.add(new JLabel("Username"), c);
        c.insets = new Insets(2, 0, 4, 0);
        form.add(txtUsername, c);
        c.insets = new Insets(8, 0, 2, 0);
        form.add(new JLabel("Password"), c);
        c.insets = new Insets(2, 0, 4, 0);
        form.add(txtPassword, c);
        c.insets = new Insets(16, 0, 4, 0);
        form.add(btnLogin, c);

        JPanel bungkus = new JPanel(new BorderLayout());
        bungkus.add(form, BorderLayout.CENTER);
        return bungkus;
    }

    private void pasangEvent() {
        btnLogin.addActionListener(e -> prosesLogin());
    }

    private void prosesLogin() {
        try {
            Petugas petugas = controller.login(txtUsername.getText(), new String(txtPassword.getPassword()));
            dispose();
            saatBerhasil.accept(petugas);
        } catch (ValidasiException ex) {
            UiUtil.pesanError(this, ex.getMessage());
            txtPassword.setText("");
            txtPassword.requestFocusInWindow();
        }
    }
}
