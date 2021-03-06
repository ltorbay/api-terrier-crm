package fr.terrier.apiterriercrm.model.entity;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Persistent;
import org.springframework.data.relational.core.mapping.Embedded;

@Getter
@Setter
@Persistent
public class PricingConfigurationEntity {
    /*
     Prices are all configured in cents, per night
     */
    @NotNull
    @Positive
    @Embedded.Empty(prefix = "both_")
    private PricingEntity both;
    @Positive
    @Embedded.Empty(prefix = "grape_")
    private PricingEntity grape;
    @Positive
    @Embedded.Empty(prefix = "pear_")
    private PricingEntity pear;

    @Getter
    @Setter
    @Persistent
    public static class PricingEntity {
        @Positive
        private Long nightly;
        @Positive
        private Long weekly;
    }
}
