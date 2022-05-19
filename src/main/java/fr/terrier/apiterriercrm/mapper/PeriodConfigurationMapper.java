package fr.terrier.apiterriercrm.mapper;

import fr.terrier.apiterriercrm.model.dto.PeriodConfiguration;
import fr.terrier.apiterriercrm.model.entity.PeriodConfigurationEntity;
import org.mapstruct.Mapper;

@Mapper
public interface PeriodConfigurationMapper {
    PeriodConfiguration map(PeriodConfigurationEntity entity);
}
