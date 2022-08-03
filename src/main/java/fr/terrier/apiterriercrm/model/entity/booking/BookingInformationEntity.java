package fr.terrier.apiterriercrm.model.entity.booking;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Persistent;

@Getter
@Setter
@Builder
@Persistent
public class BookingInformationEntity {
    private Integer guestsCount;
    @Size(max = 2000)
    private String comment;
    private String paymentSourceId;
    @Positive
    private Long paymentAmountCents;
    @Positive
    private Boolean downPayment;
    @PositiveOrZero
    private Long cleaningFeeCents;
}
