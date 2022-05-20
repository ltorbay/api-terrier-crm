package fr.terrier.apiterriercrm.model.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.Period;

@Getter
@AllArgsConstructor
public class LocalDatesPeriod {
    @NotNull
    protected LocalDate start;
    @NotNull
    protected LocalDate end;

    @AssertTrue
    @SuppressWarnings("unused")
    protected boolean valid() {
        return start.isBefore(end);
    }

    public Integer consecutiveDays() {
        return Period.between(start, end).getDays() + 1;
    }
}
