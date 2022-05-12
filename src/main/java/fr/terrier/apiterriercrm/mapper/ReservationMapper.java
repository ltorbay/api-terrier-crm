package fr.terrier.apiterriercrm.mapper;

import fr.terrier.apiterriercrm.model.dto.ReservationsResponse;
import fr.terrier.apiterriercrm.model.entity.ReservationView;
import fr.terrier.apiterriercrm.model.enums.BookingType;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class ReservationMapper {
    public ReservationsResponse map(final Set<ReservationView> reservationViews) {
        TreeSet<LocalDate> pear = new TreeSet<>();
        TreeSet<LocalDate> grapes = new TreeSet<>();

        // TODO could this be done in sql directly via the projection ?
        reservationViews.forEach(projection -> {
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
        return new ReservationsResponse()
                .pear(pear)
                .grapes(grapes);
    }
}
