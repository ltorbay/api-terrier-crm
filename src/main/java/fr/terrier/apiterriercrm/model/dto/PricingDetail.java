package fr.terrier.apiterriercrm.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.terrier.apiterriercrm.model.enums.BookingType;
import fr.terrier.apiterriercrm.model.enums.Locale;
import fr.terrier.apiterriercrm.model.exception.InternalServerException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
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

        
        var nightlyRate = Optional.ofNullable(periodConfiguration.getPricing().getNightlyRate(type))
                                  .orElseGet(() -> weeklyOptional.map(weekly -> 100 * (weekly / (100 * NIGHTS_IN_FULL_WEEK)))
                                                                 // FIXME thrown :
                                                                 // fr.terrier.apiterriercrm.model.exception.InternalServerException: 500 INTERNAL_SERVER_ERROR "Missing expected nightly rate with type GRAPE for pricing calculation on detail fr.terrier.apiterriercrm.model.dto.PricingDetail@45dea8c7"
                                                                 //	at fr.terrier.apiterriercrm.model.dto.PricingDetail.lambda$getTotalCents$1(PricingDetail.java:35) ~[classes!/:0.1.1]
                                                                 //	Suppressed: reactor.core.publisher.FluxOnAssembly$OnAssemblyException: 
                                                                 //Error has been observed at the following site(s):
                                                                 //	*__checkpoint ⇢ Handler fr.terrier.apiterriercrm.controller.BookingController#prepareBooking(BookingType, LocalDate, LocalDate) [DispatcherHandler]
                                                                 //	*__checkpoint ⇢ fr.terrier.apiterriercrm.configuration.LoggingFilter [DefaultWebFilterChain]
                                                                 .orElseThrow(() -> new InternalServerException("Missing expected nightly rate with type %s for pricing calculation on detail %s", type, this)));
        var weeklyRate = weeklyOptional.orElseGet(() -> nightlyRate * NIGHTS_IN_FULL_WEEK);

        return nightlyRate * (paidNights % NIGHTS_IN_FULL_WEEK)
                + weeklyRate * (paidNights / NIGHTS_IN_FULL_WEEK);
    }

    public String prettyPrint(final DateTimeFormatter dateFormat) {
        return String.format("Réservation %s - %s en %s - %s pour %s€",
                             bookingPeriod.getStart().format(dateFormat), bookingPeriod.getEnd().format(dateFormat),
                             periodConfiguration.getPeriodType().getLabel(Locale.FR), type.getLabel(Locale.FR),
                             getTotalCents() / 100);
    }
}
