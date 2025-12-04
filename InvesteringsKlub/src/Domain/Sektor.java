package Domain;


public enum Sektor {
    HEALTH_CARE("Health Care"),
    INDUSTRIALS("Industrials"),
    CONSUMER_GOODS("Consumer Goods"),
    FINANCIALS("Financials"),
    ENERGY("Energy"),
    UTILITIES("Utilities"),
    TECHNOLOGY("Technology");

    private final String displayName;

    Sektor(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    // Konverter fra CSV string til enum
    public static Sektor fromString(String text) {
        for (Sektor sektor : Sektor.values()) {
            if (sektor.displayName.equalsIgnoreCase(text)) {
                return sektor;
            }
        }
        throw new IllegalArgumentException("Unknown sector: " + text);
    }
}

