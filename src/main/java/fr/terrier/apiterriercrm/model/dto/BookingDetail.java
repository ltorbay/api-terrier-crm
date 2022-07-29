package fr.terrier.apiterriercrm.model.dto;

import fr.terrier.apiterriercrm.model.enums.BookingStatus;
import fr.terrier.apiterriercrm.model.enums.BookingType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingDetail {
    private Long id;
    private Long userId;
    private BookingStatus status;
    private BookingType type;
    private BookingPeriod period;
    private BookingInformation information;
}
