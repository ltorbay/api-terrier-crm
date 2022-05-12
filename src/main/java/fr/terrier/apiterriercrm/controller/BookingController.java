package fr.terrier.apiterriercrm.controller;

import fr.terrier.apiterriercrm.model.dto.BookingRequest;
import fr.terrier.apiterriercrm.model.dto.BookingResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/bookings")
public class BookingController {
    // TODO filter logging
    @PostMapping
    public BookingResponse book(@Valid @RequestBody BookingRequest request) {
        return new BookingResponse().period(request.period());
    }
}
