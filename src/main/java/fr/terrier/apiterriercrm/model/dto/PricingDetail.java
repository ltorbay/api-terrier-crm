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
    // TODO include additional fees if applicable
    @JsonProperty
    Map<PeriodConfiguration, BookingPeriod> periods;

    @JsonProperty
    public Long totalCents(BookingType type) {
        return periods.entrySet()
                      .stream()
                      .mapToLong(entry -> {
                          // TODO make calculations with full week price for saturday->saturday (selected day in properties for full week) !!
                          var paidDays = entry.getValue().consecutiveDays() - 1L;
                          var rate = Optional.ofNullable(entry.getKey().getPricing().getDailyRate(type))
                                             .orElseThrow(() -> new InternalServerException("Missing expected rate with type %s for pricing calculation on entry %s", type, entry));
                          return paidDays * rate;
                      })
                      .sum();
    }
}
