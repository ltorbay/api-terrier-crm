package fr.terrier.apiterriercrm.configuration;

import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

@MapperConfig(
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        componentModel = "spring"
)
public class CustomMapperConfiguration {
}
