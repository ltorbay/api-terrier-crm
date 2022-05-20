package fr.terrier.apiterriercrm.model.entity.booking;

import fr.terrier.apiterriercrm.model.enums.BookingStatus;
import fr.terrier.apiterriercrm.model.enums.BookingType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Persistent;
import org.springframework.data.relational.core.mapping.Embedded;

import java.util.UUID;

@Getter
@Setter
@Builder
@Persistent
public class BookingEntity {
    @Id
    private Long id;

    @Valid
    @NotNull
    private Long userId;

    @NotNull
    @Builder.Default
    private BookingStatus status = BookingStatus.CREATED;

    @NotNull
    private UUID idempotencyKey;

    @NotNull
    private BookingType type;

    @Valid
    @NotNull
    @Embedded.Empty
    private BookingPeriodEntity period;

    @Valid
    @NotNull
    @Embedded.Empty
    private BookingInformationEntity information;
}
