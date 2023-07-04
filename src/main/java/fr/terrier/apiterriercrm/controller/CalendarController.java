package fr.terrier.apiterriercrm.controller;

import biweekly.Biweekly;
import biweekly.io.chain.ChainingTextWriter;
import fr.terrier.apiterriercrm.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/public/calendar.ics")
@RequiredArgsConstructor
public class CalendarController {
    private final CalendarService calendarService;

    @GetMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    public Mono<String> getCalendar() {
        return calendarService.getCalendar()
                              .map(Biweekly::write)
                              .map(ChainingTextWriter::go);
    }
}
