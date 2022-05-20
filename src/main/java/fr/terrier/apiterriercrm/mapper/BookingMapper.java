package fr.terrier.apiterriercrm.mapper;

import fr.terrier.apiterriercrm.configuration.CustomMapperConfiguration;
import fr.terrier.apiterriercrm.model.dto.BookingRequest;
import fr.terrier.apiterriercrm.model.dto.BookingResponse;
import fr.terrier.apiterriercrm.model.entity.booking.BookingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = CustomMapperConfiguration.class)
public interface BookingMapper {
    BookingResponse map(final BookingEntity entity);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "idempotencyKey", ignore = true)
    @Mapping(target = "id", ignore = true)
    BookingEntity map(final BookingRequest request);
}
