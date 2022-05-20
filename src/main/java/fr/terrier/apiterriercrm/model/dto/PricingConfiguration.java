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
    private Long both;
    @Positive
    private Long grapes;
    @Positive
    private Long pear;

    public @Nullable Long getRate(BookingType type) {
        return switch (type) {
            case BOTH -> both;
            case GRAPES -> grapes;
            case PEAR -> pear;
        };
    }
}
