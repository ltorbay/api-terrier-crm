package fr.terrier.apiterriercrm.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingInformation {
    @NotNull
    @Positive
    private Integer guestsCount;
    @NotBlank
    private String paymentSourceId;
    @Positive
    @NotNull
    private Long paymentAmountCents;
    @NotNull
    private Boolean downPayment;
    private String comment;
}
