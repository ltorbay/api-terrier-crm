package fr.terrier.apiterriercrm.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.terrier.apiterriercrm.model.enums.BookingType;
import fr.terrier.apiterriercrm.model.exception.InternalServerException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;

@Getter
@AllArgsConstructor
public class PricingDetail {
    public static final int NIGHTS_IN_FULL_WEEK = 7;
    
    // TODO include additional fees if applicable
    @JsonProperty
    Map<PeriodConfiguration, BookingPeriod> periods;

    @JsonProperty
    // TODO unit tests
    public Long totalCents(BookingType type) {
        return periods.entrySet()
                      .stream()
                      .mapToLong(entry -> {
                          var paidNights = entry.getValue().consecutiveDays() - 1L;
                          var nightlyRate = Optional.ofNullable(entry.getKey().getPricing().getNightlyRate(type))
                                                  .orElseThrow(() -> new InternalServerException("Missing expected rate with type %s for pricing calculation on entry %s", type, entry));
                          var weeklyRate = Optional.ofNullable(entry.getKey().getPricing().getWeeklyRate(type))
                                                   .orElseGet(() -> nightlyRate * NIGHTS_IN_FULL_WEEK);
                          return nightlyRate * (paidNights % NIGHTS_IN_FULL_WEEK)
                                  + weeklyRate * (paidNights / NIGHTS_IN_FULL_WEEK);
                      })
                      .sum();
    }
}
