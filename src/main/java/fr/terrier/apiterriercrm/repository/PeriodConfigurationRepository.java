package fr.terrier.apiterriercrm.repository;

import fr.terrier.apiterriercrm.model.entity.PeriodConfigurationEntity;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PeriodConfigurationRepository extends CrudRepository<PeriodConfigurationEntity, Long> {
    // TODO test query
    @Query("""
            (select * from PeriodConfiguration where start <= :start order by start desc limit 1)
            union all
            (select * from PeriodConfiguration where start > :start and start <= :end)
            """)
    List<PeriodConfigurationEntity> findForPeriod(@Param("start") LocalDate start,
                                                  @Param("end") LocalDate end);
}
