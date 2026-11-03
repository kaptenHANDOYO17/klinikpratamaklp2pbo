package klinik.model;

/** Poli (unit layanan) di klinik pratama. Poli Gigi = layanan khusus/spesialis. */
public enum Poli {
    UMUM("Poli Umum"),
    GIGI("Poli Gigi");

    private final String label;

    Poli(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
