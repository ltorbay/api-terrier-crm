package fr.terrier.apiterriercrm.mapper;

import fr.terrier.apiterriercrm.model.dto.BookingResponse;
import fr.terrier.apiterriercrm.model.entity.BookingEntity;
import org.mapstruct.Mapper;

@Mapper
public interface BookingMapper {
    BookingResponse map(final BookingEntity bookingEntity);

    BookingEntity map(final BookingResponse bookingResponse);
}
