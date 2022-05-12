package fr.terrier.apiterriercrm.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingRequest {
    @Valid
    @NotNull
    private BookingPeriod period;
    @Valid
    @NotNull
    private User user;
    @Valid
    @NotNull
    private BookingInformation information;
}
