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
                          var paidDays = entry.getValue().consecutiveDays() - 1L;
                          var rate = Optional.ofNullable(entry.getKey().getPricing().getRate(type))
                                             .orElseThrow(() -> new InternalServerException("Missing expected rate with type %s for pricing calculation on entry %s", type, entry));
                          return paidDays * rate;
                      })
                      .sum();
    }
}
