package fr.terrier.apiterriercrm.model.enums;

public enum PricingPeriodType {
    OFF_SEASON,
    PEAK_SEASON,
    HOLIDAYS;

    public String getLabel(Locale locale) {
        return switch (this) {
            case OFF_SEASON -> Locale.FR.equals(locale) ? "Basse saison" : "Off season";
            case PEAK_SEASON -> Locale.FR.equals(locale) ? "Haute saison" : "Peak season";
            case HOLIDAYS -> Locale.FR.equals(locale) ? "Période de vacances" : "Holidays";
        };
    }
}
