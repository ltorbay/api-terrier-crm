package fr.terrier.apiterriercrm.model.entity;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Persistent;

@Getter
@Setter
@Builder
@Persistent
public class PricingConfigurationEntity {
    /*
     Prices are all configured in cents, per night
     */
    @NotNull
    @Positive
    private Long both;
    @Positive
    private Long grapes;
    @Positive
    private Long pear;
}
