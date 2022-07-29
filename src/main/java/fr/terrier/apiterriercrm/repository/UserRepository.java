package fr.terrier.apiterriercrm.repository;

import fr.terrier.apiterriercrm.model.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmailAndCrmIdNotNull(final String email);
}
