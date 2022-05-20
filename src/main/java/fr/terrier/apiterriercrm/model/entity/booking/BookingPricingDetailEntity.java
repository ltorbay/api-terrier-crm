package fr.terrier.apiterriercrm.model.entity.booking;

import fr.terrier.apiterriercrm.model.entity.BasePeriodConfigurationEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Persistent;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Builder
@Persistent
@Table("BookingPricingDetail")
public class BookingPricingDetailEntity {
    @Id
    private Long id;

    @NotNull
    private Long bookingId;
    
    @Valid
    @NotNull
    @Embedded.Empty(prefix = "configuration_")
    BasePeriodConfigurationEntity periodConfiguration;
    @Valid
    @NotNull
    @Embedded.Empty
    BookingPeriodEntity bookingPeriod;
}
