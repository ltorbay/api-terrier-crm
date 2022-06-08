package fr.terrier.apiterriercrm.controller;

import fr.terrier.apiterriercrm.model.dto.BookingDetail;
import fr.terrier.apiterriercrm.service.BookingService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@RestController
@RequestMapping("/admin/bookings")
@RequiredArgsConstructor
public class AdminBookingController {
    private final BookingService bookingService;
    @GetMapping
    public Flux<BookingDetail> getBookings(@RequestParam @NotNull final LocalDate start,
                                           @RequestParam @NotNull final LocalDate end) {
        // TODO check start before end
        return bookingService.getBookingDetail(start, end);
    }
    
}
