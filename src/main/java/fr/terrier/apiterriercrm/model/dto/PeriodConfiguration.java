package fr.terrier.apiterriercrm.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import fr.terrier.apiterriercrm.model.enums.PricingPeriodType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Embedded;

import java.time.LocalDate;

@Getter
@Setter
public class PeriodConfiguration {
    @NotNull
    private PricingPeriodType periodType;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate start;
    @NotNull
    private Integer minConsecutiveDays;
    @Valid
    @NotNull
    @Embedded.Empty
    private PricingConfiguration pricing;

}
