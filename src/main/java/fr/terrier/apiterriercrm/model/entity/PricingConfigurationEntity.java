package fr.terrier.apiterriercrm.model.entity;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Persistent;

@Getter
@Setter
@Persistent
public class PricingConfigurationEntity {
    /*
     Prices are all configured in cents, per night
     */
    @NotNull
    @Positive
    private Integer both;
    @Positive
    private Integer grapes;
    @Positive
    private Integer pear;
}
