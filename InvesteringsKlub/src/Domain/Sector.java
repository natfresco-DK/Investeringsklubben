package Domain;


public enum Sector {
    HEALTH_CARE("Health Care"),
    INDUSTRIALS("Industrials"),
    CONSUMER_GOODS("Consumer Goods"),
    FINANCIALS("Financials"),
    ENERGY("Energy"),
    UTILITIES("Utilities"),
    TECHNOLOGY("Technology");

    private final String displayName;

    Sector(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    // Konverter fra CSV string til enum
    public static Sector fromString(String text) {
        for (Sector sector : Sector.values()) {
            if (sector.displayName.equalsIgnoreCase(text)) {
                return sector;
            }
        }
        throw new IllegalArgumentException("Unknown sector: " + text);
    }
}

