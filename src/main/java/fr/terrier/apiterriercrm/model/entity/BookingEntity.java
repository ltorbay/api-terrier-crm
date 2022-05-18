package fr.terrier.apiterriercrm.model.entity;

import fr.terrier.apiterriercrm.model.enums.BookingStatus;
import fr.terrier.apiterriercrm.model.enums.BookingType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.util.Assert;

import java.util.UUID;

@Getter
@Setter
public class BookingEntity {
    @Id
    private Long id;
    
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

    @Valid
    @NotNull
    @Setter(AccessLevel.NONE)
    private Long userId;

    public BookingEntity user(UserEntity user) {
        Assert.notNull(user.id(), () -> "User id cannot be null for booking");
        this.userId = user.id();
        return this;
    }
}
