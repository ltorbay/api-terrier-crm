package fr.terrier.apiterriercrm.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class BookingDetails {
    @NonNull
    @Positive
    private Long amount;
    @NonNull
    private List<PricingDetail> pricing;
    @NonNull
    private Long cleaningFeeCents;
    @NotBlank
    private String sourceId;
    @NonNull
    private Boolean downPayment;
    @NonNull
    private Long downPaymentAmount;
}
