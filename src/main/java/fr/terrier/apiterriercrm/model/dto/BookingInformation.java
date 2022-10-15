package fr.terrier.apiterriercrm.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingInformation extends BaseBookingInformation {
    @NotBlank
    private String paymentSourceId;
}
