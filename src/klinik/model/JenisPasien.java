package klinik.model;

/** Jenis pembiayaan pasien: BPJS (ditanggung) atau Umum (bayar sendiri). */
public enum JenisPasien {
    BPJS("BPJS"),
    UMUM("Umum");

    private final String label;

    JenisPasien(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
