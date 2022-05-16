package fr.terrier.apiterriercrm.repository;

import fr.terrier.apiterriercrm.model.entity.AvailabilityView;
import fr.terrier.apiterriercrm.model.entity.BookingEntity;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Set;

@Repository
public interface BookingRepository extends CrudRepository<BookingEntity, Long> {

    @Query("Select type, start, end from Booking where :start <= end and :end >= start")
    Set<AvailabilityView> findByPeriodBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);
}
