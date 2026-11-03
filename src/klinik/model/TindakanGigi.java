package klinik.model;

/**
 * Daftar tindakan di Poli Gigi beserta harga dan status tanggungan BPJS.
 * Bleaching (pemutihan) adalah tindakan estetika, jadi TIDAK ditanggung BPJS.
 */
public enum TindakanGigi {
    KONSULTASI("Konsultasi saja", 0, true),
    TAMBAL("Tambal Gigi", 150000, true),
    CABUT("Cabut Gigi", 200000, true),
    SCALING("Scaling (Pembersihan Karang)", 250000, true),
    BLEACHING("Bleaching (Pemutihan Gigi)", 750000, false);

    private final String label;
    private final double harga;
    private final boolean ditanggungBpjs;

    TindakanGigi(String label, double harga, boolean ditanggungBpjs) {
        this.label = label;
        this.harga = harga;
        this.ditanggungBpjs = ditanggungBpjs;
    }

    public double getHarga() {
        return harga;
    }

    public boolean isDitanggungBpjs() {
        return ditanggungBpjs;
    }

    @Override
    public String toString() {
        return label;
    }
}
