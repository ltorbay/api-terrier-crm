package fr.terrier.apiterriercrm.service;

import fr.terrier.apiterriercrm.mapper.PeriodConfigurationMapper;
import fr.terrier.apiterriercrm.model.dto.BookingPeriod;
import fr.terrier.apiterriercrm.model.dto.PeriodConfiguration;
import fr.terrier.apiterriercrm.model.dto.PriceDetail;
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class PricingService {
    private final PeriodConfigurationMapper periodConfigurationMapper;
    private final PeriodConfigurationRepository periodConfigurationRepository;
    @Qualifier("datasourceScheduler")
    private final Scheduler datasourceScheduler;

    public Flux<PeriodConfiguration> getPricingPattern(final LocalDate start, LocalDate end) {
        // noinspection BlockingMethodInNonBlockingContext
        return Mono.fromCallable(() -> periodConfigurationRepository.findForPeriod(start, end))
                   .subscribeOn(datasourceScheduler)
                   .flatMapIterable(l -> l)
                   .map(periodConfigurationMapper::map);
    }

    public Mono<Long> computeAmountCents(final BookingType type, final BookingPeriod period) {
        return getBookingPriceDetail(type, period)
                .map(detail -> detail.totalCents(type));
    }

    public Mono<PriceDetail> getBookingPriceDetail(final BookingType type, final BookingPeriod period) {
        return getPricingPattern(period.start(), period.end())
                .handle((PeriodConfiguration periodConfiguration, SynchronousSink<PeriodConfiguration> sink) -> {
                    var rate = periodConfiguration.period().rate(type);
                    if (rate == null) {
                        sink.error(new ResponseException(HttpStatus.BAD_REQUEST,
                                                         String.format("Period %s cannot be booked fo type %s. No matching rate exist in configuration", period, type)));
                    }

                    var consecutiveDays = period.consecutiveDays();
                    if (consecutiveDays < periodConfiguration.minConsecutiveDays()) {
                        sink.error(new ResponseException(HttpStatus.BAD_REQUEST,
                                                         String.format("Period %s cannot be booked fo type %s. Minimal consecutive days requirement not met", period, type)));
                    }
                    sink.next(periodConfiguration);
                })
                .collectSortedList(Comparator.comparing(PeriodConfiguration::start))
                .map(configurations -> {
                    Map<PeriodConfiguration, BookingPeriod> detailedPricing = new HashMap<>();
                    var previous = new AtomicReference<>(period.start());
                    IntStream.range(0, configurations.size())
                             .forEach(i -> {
                                 var end = i + 1 >= configurations.size() ? period.end() : configurations.get(i + 1).start().minusDays(1L);
                                 detailedPricing.put(configurations.get(i), new BookingPeriod(previous.get(), end));
                                 previous.set(end.plusDays(1L));
                             });
                    return new PriceDetail(detailedPricing);
                });
    }
}
