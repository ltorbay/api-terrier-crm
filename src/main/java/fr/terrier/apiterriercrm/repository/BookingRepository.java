package fr.terrier.apiterriercrm.repository;

import fr.terrier.apiterriercrm.model.entity.AvailabilityView;
import fr.terrier.apiterriercrm.model.entity.booking.BookingEntity;
import fr.terrier.apiterriercrm.model.enums.BookingStatus;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

@Repository
public interface BookingRepository extends CrudRepository<BookingEntity, Long> {

    @Modifying
    @Query("update Booking set status = :status, paymentId = :paymentId where id = :id")
    Optional<BookingEntity> persistBookingPayment(@Param("newStatus") BookingStatus newStatus,
                                                  @Param("paymentId") String paymentId,
                                                  @Param("id") Long id);

    @Query("select type, start, end from Booking where :start <= end and :end >= start")
    Set<AvailabilityView> findByPeriodBetween(@Param("start") LocalDate start,
                                              @Param("end") LocalDate end);
}
