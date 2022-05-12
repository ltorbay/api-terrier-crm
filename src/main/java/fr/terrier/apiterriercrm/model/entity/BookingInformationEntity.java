package fr.terrier.apiterriercrm.model.entity;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Persistent;

@Getter
@Setter
@Persistent
public class BookingInformationEntity {
    private Integer guestsCount;
    @Size(max = 2000)
    private String comment;
}
