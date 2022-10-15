package fr.terrier.apiterriercrm.controller;

import fr.terrier.apiterriercrm.model.dto.AdminBookingInformation;
import fr.terrier.apiterriercrm.model.dto.BaseBookingInformation;
import fr.terrier.apiterriercrm.model.dto.BookingDetail;
import fr.terrier.apiterriercrm.model.dto.BookingInformation;
import fr.terrier.apiterriercrm.model.dto.BookingRequest;
import fr.terrier.apiterriercrm.model.dto.BookingResponse;
import fr.terrier.apiterriercrm.model.exception.BadRequestException;
import fr.terrier.apiterriercrm.service.BookingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@RestController
@RequestMapping("/admin/bookings")
@RequiredArgsConstructor
public class AdminBookingController {
    private final BookingService bookingService;

    @GetMapping
    public Flux<BookingDetail> getBookings(@RequestParam @NotNull final LocalDate start,
                                           @RequestParam @NotNull final LocalDate end) {
        if (start.isAfter(end)) {
            return Flux.error(new BadRequestException("Queried start date is after end date"));
        }
        
        return bookingService.getBookingDetail(start, end);
    }

    @PostMapping
    public Mono<BookingResponse> book(@Valid @RequestBody BookingRequest<AdminBookingInformation> bookingRequest) {
        return bookingService.book(bookingRequest);
    }
}
