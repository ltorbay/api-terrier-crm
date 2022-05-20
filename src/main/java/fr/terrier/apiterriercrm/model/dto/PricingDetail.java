package fr.terrier.apiterriercrm.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.terrier.apiterriercrm.model.enums.BookingType;
import fr.terrier.apiterriercrm.model.exception.InternalServerException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

@Getter
@AllArgsConstructor
public class PricingDetail {
    public static final int NIGHTS_IN_FULL_WEEK = 7;

    @JsonProperty
    private PeriodConfiguration periodConfiguration;
    @JsonProperty
    private BookingPeriod bookingPeriod;

    @JsonProperty
    // TODO unit tests
    public Long totalCents(BookingType type) {
        var paidNights = bookingPeriod.consecutiveDays() - 1L;
        var nightlyRate = Optional.ofNullable(periodConfiguration.getPricing().getNightlyRate(type))
                                  .orElseThrow(() -> new InternalServerException("Missing expected rate with type %s for pricing calculation on detail %s", type, this));
        var weeklyRate = Optional.ofNullable(periodConfiguration.getPricing().getWeeklyRate(type))
                                 .orElseGet(() -> nightlyRate * NIGHTS_IN_FULL_WEEK);
        return nightlyRate * (paidNights % NIGHTS_IN_FULL_WEEK)
                + weeklyRate * (paidNights / NIGHTS_IN_FULL_WEEK);
    }
}
