package fr.terrier.apiterriercrm.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    private BookingType type;

    @JsonProperty
    // TODO unit tests
    public Long getTotalCents() {
        var paidNights = bookingPeriod.consecutiveDays() - 1L;
        var weeklyOptional = Optional.ofNullable(periodConfiguration.getPricing().getWeeklyRate(type));

        // FIXME Do not make calculation for last day of period as it is not used in the total price !
        var nightlyRate = Optional.ofNullable(periodConfiguration.getPricing().getNightlyRate(type))
                                  .orElseGet(() -> weeklyOptional.map(weekly -> 100 * (weekly / (100 * NIGHTS_IN_FULL_WEEK)))
                                                                 .orElseThrow(() -> new InternalServerException("Missing expected nightly rate with type %s for pricing calculation on detail %s", type, this)));
        var weeklyRate = weeklyOptional.orElseGet(() -> nightlyRate * NIGHTS_IN_FULL_WEEK);

        return nightlyRate * (paidNights % NIGHTS_IN_FULL_WEEK)
                + weeklyRate * (paidNights / NIGHTS_IN_FULL_WEEK);
    }
}
