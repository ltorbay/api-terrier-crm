package fr.terrier.apiterriercrm.mapper;

import fr.terrier.apiterriercrm.model.dto.BookedDates;
import fr.terrier.apiterriercrm.model.entity.BookedPeriodView;
import fr.terrier.apiterriercrm.model.enums.BookingType;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class BookedPeriodMapper {
    public BookedDates map(final Set<BookedPeriodView> bookedPeriodViews) {
        TreeSet<LocalDate> pear = new TreeSet<>();
        TreeSet<LocalDate> grapes = new TreeSet<>();

        bookedPeriodViews.forEach(projection -> {
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
        return BookedDates.builder()
                          .pearBookings(pear)
                          .grapesBookings(grapes)
                          .build();
    }
}
