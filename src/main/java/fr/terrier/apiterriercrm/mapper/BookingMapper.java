package fr.terrier.apiterriercrm.mapper;

import fr.terrier.apiterriercrm.configuration.CustomMapperConfiguration;
import fr.terrier.apiterriercrm.model.dto.BookingDetail;
import fr.terrier.apiterriercrm.model.dto.BookingRequest;
import fr.terrier.apiterriercrm.model.dto.BookingResponse;
import fr.terrier.apiterriercrm.model.entity.booking.BookingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = CustomMapperConfiguration.class)
public interface BookingMapper {
    BookingResponse entityToResponse(final BookingEntity entity);
    
    BookingDetail entityToDetail(final BookingEntity entity);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "id", ignore = true)
    BookingEntity entityToResponse(final BookingRequest request);
}
