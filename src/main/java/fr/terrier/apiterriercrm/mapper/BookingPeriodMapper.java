package fr.terrier.apiterriercrm.mapper;

import fr.terrier.apiterriercrm.configuration.CustomMapperConfiguration;
import fr.terrier.apiterriercrm.model.dto.PeriodConfiguration;
import fr.terrier.apiterriercrm.model.entity.BasePeriodConfigurationEntity;
import org.mapstruct.Mapper;

@Mapper(config = CustomMapperConfiguration.class)
public interface BookingPeriodMapper {
    BasePeriodConfigurationEntity map(final PeriodConfiguration periodConfiguration);
}
