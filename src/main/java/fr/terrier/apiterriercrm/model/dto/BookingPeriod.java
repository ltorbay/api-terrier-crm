package fr.terrier.apiterriercrm.model.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
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
