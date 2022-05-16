package fr.terrier.apiterriercrm.controller;

import fr.terrier.apiterriercrm.mapper.AvailabilityMapper;
import fr.terrier.apiterriercrm.model.dto.AvailabilityResponse;
import fr.terrier.apiterriercrm.repository.BookingRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.time.LocalDate;

@RestController
@RequestMapping("/public/availabilities")
@RequiredArgsConstructor
public class AvailabilityController {
    @Qualifier("datasourceScheduler")
    private final Scheduler datasourceScheduler;
    private final BookingRepository bookingRepository;
    private final AvailabilityMapper availabilityMapper;

    @GetMapping
    public Mono<AvailabilityResponse> getAvailability(@RequestParam @NotNull final LocalDate start,
                                                      @RequestParam @NotNull final LocalDate end) {
        // noinspection BlockingMethodInNonBlockingContext
        return Mono.fromCallable(() -> bookingRepository.findByPeriodBetween(start, end))
                   .map(availabilityMapper::map)
                   .subscribeOn(datasourceScheduler);
    }
}
