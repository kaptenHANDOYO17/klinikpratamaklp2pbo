package klinik.view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JButton;

/**
 * Tombol berwarna dengan sudut membulat.
 * Dibuat sendiri agar warnanya sama di Windows, Mac, maupun Linux.
 */
public class Tombol extends JButton {

    private static final long serialVersionUID = 1L;

    private Color warna;

    public Tombol(String teks, Color warna) {
        super(teks);
        this.warna = warna;
        setForeground(Color.WHITE);
        setFont(getFont().deriveFont(Font.BOLD));
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public void setWarna(Color warna) {
        this.warna = warna;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color isi = warna;
        if (!isEnabled()) {
            isi = Color.LIGHT_GRAY;
        } else if (getModel().isPressed()) {
            isi = warna.darker();
        } else if (getModel().isRollover()) {
            isi = warna.brighter();
        }
        g2.setColor(isi);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        g2.dispose();
        super.paintComponent(g);
    }
}
