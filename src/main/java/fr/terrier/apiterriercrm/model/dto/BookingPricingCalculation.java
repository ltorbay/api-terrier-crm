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
    private Long totalCents;
    @JsonProperty
    private Long downPaymentTotalCents;

    public BookingPricingCalculation(final List<PricingDetail> detail, final Double downPaymentRatio) {
        this.detail = detail;
        this.totalCents = detail.stream()
                                .mapToLong(PricingDetail::getTotalCents)
                                .sum();
        this.downPaymentTotalCents = ((Double) (totalCents * downPaymentRatio)).longValue();
    }

}
