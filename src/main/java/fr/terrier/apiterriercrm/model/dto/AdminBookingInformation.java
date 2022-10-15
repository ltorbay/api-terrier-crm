package fr.terrier.apiterriercrm.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminBookingInformation extends BaseBookingInformation {
    private String invoiceId;
    private String paymentSourceId;
}
