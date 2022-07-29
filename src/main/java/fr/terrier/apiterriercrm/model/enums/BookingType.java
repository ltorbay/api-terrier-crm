package fr.terrier.apiterriercrm.model.enums;

public enum BookingType {
    PEAR,
    GRAPE,
    BOTH;
    
    public String getLabel(Locale locale) {
        return switch (this) {
            case PEAR -> Locale.FR.equals(locale) ? "Gîte de la poire" : "Pear house";
            case GRAPE -> Locale.FR.equals(locale) ? "Gîte du raisin" : "Grape house";
            case BOTH -> Locale.FR.equals(locale) ? "Ensemble de la propriété" : "Whole domain";
        };
    }
}
