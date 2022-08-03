package fr.terrier.apiterriercrm.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingPricingCalculation {
    @JsonProperty
    private List<PricingDetail> detail;
    @JsonProperty
    private Long cleaningFeeCents;
    @JsonProperty
    private Long totalCents;
    @JsonProperty
    private Long downPaymentTotalCents;

    public BookingPricingCalculation(final List<PricingDetail> detail, final Long cleaningFeeCents, final Double downPaymentRatio) {
        this.detail = detail;
        this.cleaningFeeCents = cleaningFeeCents;
        this.totalCents = detail.stream()
                                .mapToLong(PricingDetail::getTotalCents)
                                .sum() + cleaningFeeCents;
        this.downPaymentTotalCents = ((Double) (totalCents * downPaymentRatio)).longValue();
    }

}
