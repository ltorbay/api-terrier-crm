package fr.terrier.apiterriercrm.controller;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import fr.terrier.apiterriercrm.mapper.ReservationMapper;
import fr.terrier.apiterriercrm.model.dto.ReservationsResponse;
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
@RequestMapping("/public/reservations")
@RequiredArgsConstructor
public class ReservationController {
    @Qualifier("datasourceScheduler")
    private final Scheduler datasourceScheduler;
    private final BookingRepository bookingRepository;
    private final ReservationMapper reservationMapper;

    @GetMapping
    public Mono<ReservationsResponse> getReservations(@RequestParam @NotNull @JsonDeserialize(using = LocalDateDeserializer.class) final LocalDate start, 
                                                      @RequestParam @NotNull @JsonDeserialize(using = LocalDateDeserializer.class) final LocalDate end) {
        // noinspection BlockingMethodInNonBlockingContext
        return Mono.fromCallable(() -> bookingRepository.findByPeriodBetween(start, end))
                   .map(reservationMapper::map)
                   .subscribeOn(datasourceScheduler);
    }
}
