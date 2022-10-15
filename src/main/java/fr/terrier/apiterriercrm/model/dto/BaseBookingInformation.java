package fr.terrier.apiterriercrm.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BaseBookingInformation {
    @NotNull
    @Positive
    private Integer guestsCount;
    @Positive
    @NotNull
    private Long paymentAmountCents;
    @NotNull
    private Boolean downPayment;
    private String comment;
    @PositiveOrZero
    private Long cleaningFeeCents;
}
