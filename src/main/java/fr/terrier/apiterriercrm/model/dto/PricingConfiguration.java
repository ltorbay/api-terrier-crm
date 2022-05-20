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
    private Pricing grapes;
    @Positive
    private Pricing pear;

    public @Nullable Long getDailyRate(BookingType type) {
        return switch (type) {
            case BOTH -> both.nightly;
            case GRAPES -> grapes.nightly;
            case PEAR -> pear.nightly;
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
