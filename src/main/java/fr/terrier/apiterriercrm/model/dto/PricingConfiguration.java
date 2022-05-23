package fr.terrier.apiterriercrm.model.dto;

import fr.terrier.apiterriercrm.model.enums.BookingType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
public class PricingConfiguration {
    @NotNull
    @Positive
    private Pricing both;
    @Positive
    private Pricing grape;
    @Positive
    private Pricing pear;

    public @Nullable Long getNightlyRate(BookingType type) {
        return switch (type) {
            case BOTH -> both.nightly;
            case GRAPE -> grape == null ? null : grape.nightly;
            case PEAR -> pear == null ? null : pear.nightly;
        };
    }

    public @Nullable Long getWeeklyRate(BookingType type) {
        return switch (type) {
            case BOTH -> both.weekly;
            case GRAPE -> grape == null ? null : grape.weekly;
            case PEAR -> pear == null ? null : pear.weekly;
        };
    }

    @Getter
    @Setter
    public static class Pricing {
        @NotNull
        @Positive
        private Long nightly;
        @NotNull
        @Positive
        private Long weekly;
    }
}
