package fr.terrier.apiterriercrm.mapper;

import fr.terrier.apiterriercrm.model.dto.AvailabilityResponse;
import fr.terrier.apiterriercrm.model.entity.AvailabilityView;
import fr.terrier.apiterriercrm.model.enums.BookingType;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class AvailabilityMapper {
    public AvailabilityResponse map(final Set<AvailabilityView> availabilityViews) {
        TreeSet<LocalDate> pear = new TreeSet<>();
        TreeSet<LocalDate> grapes = new TreeSet<>();

        availabilityViews.forEach(projection -> {
            var dates = projection.getStart()
                                  .datesUntil(projection.getEnd())
                                  .collect(Collectors.toUnmodifiableSet());
            if (!BookingType.GRAPES.equals(projection.getType())) {
                pear.addAll(dates);
            }
            if (!BookingType.PEAR.equals(projection.getType())) {
                grapes.addAll(dates);
            }
        });
        return AvailabilityResponse.builder()
                                   .pear(pear)
                                   .grapes(grapes)
                                   .build();
    }
}
