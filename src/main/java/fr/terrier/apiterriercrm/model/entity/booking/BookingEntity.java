package fr.terrier.apiterriercrm.model.entity.booking;

import fr.terrier.apiterriercrm.model.entity.UserEntity;
import fr.terrier.apiterriercrm.model.enums.BookingStatus;
import fr.terrier.apiterriercrm.model.enums.BookingType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Persistent;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.util.Assert;

import java.util.UUID;

@Getter
@Setter
@Persistent
public class BookingEntity {
    @Id
    private Long id;

    @Valid
    @NotNull
    @Setter(AccessLevel.NONE)
    private Long userId;

    @NotNull
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

    public BookingEntity user(UserEntity user) {
        Assert.notNull(user.id(), () -> "User id cannot be null for booking");
        this.userId = user.id();
        return this;
    }
}
