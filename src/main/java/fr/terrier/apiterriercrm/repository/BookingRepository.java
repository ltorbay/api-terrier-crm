package fr.terrier.apiterriercrm.repository;

import fr.terrier.apiterriercrm.model.entity.BookedPeriodView;
import fr.terrier.apiterriercrm.model.entity.booking.BookingEntity;
import fr.terrier.apiterriercrm.model.enums.BookingStatus;
import fr.terrier.apiterriercrm.model.enums.BookingType;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Set;

@Repository
public interface BookingRepository extends CrudRepository<BookingEntity, Long> {

    @Modifying
    @Query("update Booking set status = :newStatus, invoice_id = :invoiceId where id = :id")
    Boolean persistBookingPayment(@Param("newStatus") BookingStatus newStatus,
                                  @Param("invoiceId") String invoiceId,
                                  @Param("id") Long id);

    @Query("select type, start, end from Booking where :start <= end and :end >= start")
    Set<BookedPeriodView> findPeriodViewByPeriodBetween(@Param("start") LocalDate start,
                                                        @Param("end") LocalDate end);

    @Query("select type, start, end from Booking where (type = :type or type = 'BOTH') and :start <= end and :end >= start")
    Set<BookedPeriodView> findPeriodViewByTypeAndPeriodBetween(@Param("type") BookingType type,
                                                               @Param("start") LocalDate start,
                                                               @Param("end") LocalDate end);

    @Query("select * from Booking where :start <= end and :end >= start")
    Set<BookingEntity> findByPeriodBetween(@Param("start") LocalDate start,
                                           @Param("end") LocalDate end);
}
