package fr.terrier.apiterriercrm.model.entity;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Persistent;
import org.springframework.data.relational.core.mapping.Embedded;

import java.time.LocalDate;

@Getter
@Setter
@Persistent
public class PeriodConfigurationEntity {
    @Id
    private Long id;

    @NotNull
    private LocalDate start;

    @NotNull
    private Integer minConsecutiveDays;

    @Valid
    @NotNull
    @Embedded.Empty
    private PricingConfigurationEntity period;
}
