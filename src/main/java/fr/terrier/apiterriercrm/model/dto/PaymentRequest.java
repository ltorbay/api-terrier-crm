package fr.terrier.apiterriercrm.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PaymentRequest {
    @NonNull
    @Positive
    private Long amount;
    
    @NotBlank
    private String sourceId;
    
    @NonNull
    private UUID idempotencyKey;
}
