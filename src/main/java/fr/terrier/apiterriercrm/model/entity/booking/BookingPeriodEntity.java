package fr.terrier.apiterriercrm.model.entity.booking;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Persistent;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@Persistent
public class BookingPeriodEntity {
    @NotNull
    private LocalDate start;
    
    @NotNull
    private LocalDate end;
}
