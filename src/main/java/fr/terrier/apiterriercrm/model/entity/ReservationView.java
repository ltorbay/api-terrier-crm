package fr.terrier.apiterriercrm.model.entity;

import fr.terrier.apiterriercrm.model.enums.BookingType;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;

public interface ReservationView {
    BookingType getType();

    @Value("#{target.period.start}")
    LocalDate getStart();

    @Value("#{target.period.end}")
    LocalDate getEnd();
}
