package fr.terrier.apiterriercrm.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class PaymentRequest {
    @NonNull
    @Positive
    private Long amount;
    
    @NonNull
    private PricingDetail detail;
    
    @NotBlank
    private String sourceId;
    
    @NonNull
    private UUID idempotencyKey;
}
