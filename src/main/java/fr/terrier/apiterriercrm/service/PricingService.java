package fr.terrier.apiterriercrm.service;

import fr.terrier.apiterriercrm.mapper.PeriodConfigurationMapper;
import fr.terrier.apiterriercrm.model.dto.BookingPeriod;
import fr.terrier.apiterriercrm.model.dto.PeriodConfiguration;
import fr.terrier.apiterriercrm.model.dto.PricingDetail;
import fr.terrier.apiterriercrm.model.enums.BookingType;
import fr.terrier.apiterriercrm.model.exception.ResponseException;
import fr.terrier.apiterriercrm.repository.PeriodConfigurationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;
import reactor.core.scheduler.Scheduler;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class PricingService {
    private final PeriodConfigurationMapper periodConfigurationMapper;
    private final PeriodConfigurationRepository periodConfigurationRepository;
    @Qualifier("datasourceScheduler")
    private final Scheduler datasourceScheduler;

    // TODO cache : https://www.baeldung.com/spring-webflux-cacheable
    public Flux<PeriodConfiguration> getPricingPattern(final LocalDate start, LocalDate end) {
        // noinspection BlockingMethodInNonBlockingContext
        return Mono.fromCallable(() -> periodConfigurationRepository.findForPeriod(start, end))
                   .subscribeOn(datasourceScheduler)
                   .flatMapIterable(l -> l)
                   .map(periodConfigurationMapper::map);
    }

    public Mono<List<PricingDetail>> getBookingPriceDetails(final BookingType type, final LocalDate start, LocalDate end) {
        // Last day has no night -> it is free !
        return getPricingPattern(start, end.minusDays(1L))
                .handle((PeriodConfiguration periodConfiguration, SynchronousSink<PeriodConfiguration> sink) -> {
                    var consecutiveDays = ChronoUnit.DAYS.between(start, end) + 2;
                    if (consecutiveDays < periodConfiguration.getMinConsecutiveDays()) {
                        sink.error(new ResponseException(HttpStatus.BAD_REQUEST,
                                                         String.format("Period %s - %s cannot be booked fo type %s. Minimal consecutive days requirement not met", start, end, type)));
                        return;
                    }
                    sink.next(periodConfiguration);
                })
                .collectSortedList(Comparator.comparing(PeriodConfiguration::getStart))
                .map(configurations -> {
                    var details = new ArrayList<PricingDetail>();
                    var previous = new AtomicReference<>(start);
                    IntStream.range(0, configurations.size())
                             .forEach(i -> {
                                 var detailEnd = i + 1 >= configurations.size() ? end : configurations.get(i + 1).getStart();
                                 details.add(new PricingDetail(configurations.get(i), new BookingPeriod(previous.get(), detailEnd), type));
                                 previous.set(detailEnd);
                             });
                    return details;
                });
    }
}
