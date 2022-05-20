package fr.terrier.apiterriercrm.mapper;

import fr.terrier.apiterriercrm.configuration.CustomMapperConfiguration;
import fr.terrier.apiterriercrm.model.dto.PeriodConfiguration;
import fr.terrier.apiterriercrm.model.entity.PeriodConfigurationEntity;
import org.mapstruct.Mapper;

@Mapper(config = CustomMapperConfiguration.class)
public interface PeriodConfigurationMapper {
    PeriodConfiguration map(PeriodConfigurationEntity entity);
}
