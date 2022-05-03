package fr.terrier.apiterriercrm.model.dto;

import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.time.Period;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class BookingPeriod {
    private List<Date> weekStarts;
    private List<Period> periods;

    @AssertTrue
    @SuppressWarnings("unused")
    private boolean valid() {
        return !CollectionUtils.isEmpty(weekStarts)
                || !CollectionUtils.isEmpty(periods);
    }
}
