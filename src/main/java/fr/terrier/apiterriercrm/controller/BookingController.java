package fr.terrier.apiterriercrm.controller;

import fr.terrier.apiterriercrm.model.dto.BookedDates;
import fr.terrier.apiterriercrm.model.dto.BookingPricingCalculation;
import fr.terrier.apiterriercrm.model.dto.BookingRequest;
import fr.terrier.apiterriercrm.model.dto.BookingResponse;
import fr.terrier.apiterriercrm.model.dto.PricingDetail;
import fr.terrier.apiterriercrm.model.enums.BookingType;
import fr.terrier.apiterriercrm.model.exception.BadRequestException;
import fr.terrier.apiterriercrm.service.BookingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/public/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public Mono<BookingResponse> book(@Valid @RequestBody BookingRequest bookingRequest) {
        return bookingService.book(bookingRequest);
    }

    @GetMapping
    public Mono<BookedDates> getBookedDates(@RequestParam @NotNull final LocalDate start,
                                            @RequestParam @NotNull final LocalDate end) {
        if(start.isAfter(end)) {
            return Mono.error(new BadRequestException("Queried start date is after end date"));
        }
        return bookingService.getBookedDates(start, end);
    }

    @GetMapping("/simulations")
    public Mono<BookingPricingCalculation> prepareBooking(@NotNull @Valid @RequestParam BookingType type,
                                                          @RequestParam @NotNull final LocalDate start,
                                                          @RequestParam @NotNull final LocalDate end) {
        if(start.isAfter(end)) {
            return Mono.error(new BadRequestException("Queried start date is after end date"));
        }
        return bookingService.getBookingPriceDetails(type, start, end);
    }
}
