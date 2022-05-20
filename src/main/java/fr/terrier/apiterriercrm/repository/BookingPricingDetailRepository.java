package fr.terrier.apiterriercrm.repository;

import fr.terrier.apiterriercrm.model.entity.booking.BookingPricingDetailEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingPricingDetailRepository extends CrudRepository<BookingPricingDetailEntity, Long> {
}
