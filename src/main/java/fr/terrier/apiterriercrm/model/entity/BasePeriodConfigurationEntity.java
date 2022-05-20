package fr.terrier.apiterriercrm.model.entity;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Persistent;
import org.springframework.data.relational.core.mapping.Embedded;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Persistent
public class BasePeriodConfigurationEntity {
    @NotNull
    private LocalDate start;

    @Valid
    @NotNull
    @Embedded.Empty(prefix = "pricing_")
    private PricingConfigurationEntity pricing;
}
