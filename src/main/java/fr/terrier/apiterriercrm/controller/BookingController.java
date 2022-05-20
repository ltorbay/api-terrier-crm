package fr.terrier.apiterriercrm.controller;

import fr.terrier.apiterriercrm.model.dto.BookingPeriod;
import fr.terrier.apiterriercrm.model.dto.BookingRequest;
import fr.terrier.apiterriercrm.model.dto.BookingResponse;
import fr.terrier.apiterriercrm.model.dto.PricingDetail;
import fr.terrier.apiterriercrm.model.enums.BookingType;
import fr.terrier.apiterriercrm.service.BookingService;
import fr.terrier.apiterriercrm.service.PricingService;
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

import java.util.List;

@RestController
@RequestMapping("/public/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final PricingService pricingService;

    // TODO filter logging
    @PostMapping
    public Mono<BookingResponse> book(@Valid @RequestBody BookingRequest bookingRequest) {
        return bookingService.book(bookingRequest);
    }

    @GetMapping
    public Mono<List<PricingDetail>> getPriceDetail(@NotNull @Valid @ModelAttribute BookingType type,
                                                    @NotNull @Valid @ModelAttribute BookingPeriod period) {
        return pricingService.getBookingPriceDetails(type, period);
    }
}
