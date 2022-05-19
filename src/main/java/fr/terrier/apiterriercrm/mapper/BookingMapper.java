package fr.terrier.apiterriercrm.mapper;

import fr.terrier.apiterriercrm.model.dto.BookingResponse;
import fr.terrier.apiterriercrm.model.entity.booking.BookingEntity;
import org.mapstruct.Mapper;

@Mapper
public interface BookingMapper {
    BookingResponse map(final BookingEntity entity);

    // TODO unmapped target configuration (should fail on unmapped!) and required annotation
    BookingEntity map(final BookingResponse dto);
}
