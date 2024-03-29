package fr.terrier.apiterriercrm.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Getter
@AllArgsConstructor
public class BookingPeriod {

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate start;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate end;

    @AssertTrue
    @SuppressWarnings("unused")
    private boolean valid() {
        return start.isBefore(end);
    }

    public Long consecutiveDays() {
        return consecutiveNights() + 1L;
    }
    
    public Long consecutiveNights() {
        return ChronoUnit.DAYS.between(start, end);
    }
}
