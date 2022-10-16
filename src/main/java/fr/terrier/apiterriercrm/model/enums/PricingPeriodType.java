package fr.terrier.apiterriercrm.model.enums;

public enum PricingPeriodType {
    OFF_SEASON,
    PEAK_SEASON,
    MID_SEASON;

    public String getLabel(Locale locale) {
        return switch (this) {
            case OFF_SEASON -> Locale.FR.equals(locale) ? "Basse saison" : "Off season";
            case PEAK_SEASON -> Locale.FR.equals(locale) ? "Haute saison" : "Peak season";
            case MID_SEASON -> Locale.FR.equals(locale) ? "Mi saison" : "Mid season";
        };
    }
}
