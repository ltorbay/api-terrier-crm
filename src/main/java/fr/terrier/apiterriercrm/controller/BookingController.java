package fr.terrier.apiterriercrm.controller;

import fr.terrier.apiterriercrm.model.dto.BookingPeriod;
import fr.terrier.apiterriercrm.model.dto.BookingRequest;
import fr.terrier.apiterriercrm.model.dto.BookingResponse;
import fr.terrier.apiterriercrm.model.enums.BookingType;
import fr.terrier.apiterriercrm.service.BookingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/public/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    // TODO filter logging
    @PostMapping
    public Mono<BookingResponse> book(@Valid @RequestBody BookingRequest bookingRequest) {
        // TODO check that number of days is enough given the configuration (minimal reservation days for low/high season)
        return bookingService.book(bookingRequest);
    }

    @GetMapping
    public Mono<BookingResponse> computeBooking(@NotNull @Valid @ModelAttribute BookingType type,
                                                @NotNull @Valid @ModelAttribute BookingPeriod period) {
        return bookingService.computeBooking(type, period);
    }

    // TODO query preflight to get payment amount (avoid calculating it in the frontend)
}
