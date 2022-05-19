package fr.terrier.apiterriercrm.model.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Period;

@Getter
@AllArgsConstructor
public class BookingPeriod {
    
    @NotNull
    private LocalDate start;
    @NotNull
    private LocalDate end;

    @AssertTrue
    @SuppressWarnings("unused")
    private boolean valid() {
        return start.isBefore(end);
    }
    
    public Integer consecutiveDays() {
        return Period.between(start, end).getDays() + 1;
    }
    
    // FIXME used in frontend for now
//    private List<LocalDate> weekStarts;
//    private List<Period> periods;
//
//    @AssertTrue
//    @SuppressWarnings("unused")
//    private boolean valid() {
//        return !CollectionUtils.isEmpty(weekStarts)
//                || !CollectionUtils.isEmpty(periods);
//    }
}
