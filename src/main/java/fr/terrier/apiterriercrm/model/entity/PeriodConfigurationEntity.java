package fr.terrier.apiterriercrm.model.entity;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Persistent;

@Getter
@Setter
@Persistent
public class PeriodConfigurationEntity extends BasePeriodConfigurationEntity {
    @Id
    private Long id;

    @NotNull
    private Integer minConsecutiveDays;
}
