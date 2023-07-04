package fr.terrier.apiterriercrm.service;

import biweekly.ICalendar;
import biweekly.component.VEvent;
import fr.terrier.apiterriercrm.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class CalendarService {
    private final BookingRepository bookingRepository;

    @Qualifier("datasourceScheduler")
    private final Scheduler datasourceScheduler;

    public Mono<ICalendar> getCalendar() {
        ICalendar calendar = new ICalendar();
        // noinspection BlockingMethodInNonBlockingContext
        return Mono.fromCallable(() -> bookingRepository.findPeriodViewByPeriodBetween(LocalDate.now(),
                                                                                       LocalDate.now().plusYears(1)))
                   .subscribeOn(datasourceScheduler)
                   .flatMapMany(Flux::fromIterable)
                   .map(period -> {
                       VEvent event = new VEvent();
                       event.setSummary("Booked period");
                       event.setDateStart(Date.from(period.getStart()
                                                          .atStartOfDay(ZoneId.systemDefault())
                                                          .toInstant()));
                       event.setDateEnd(Date.from(period.getEnd()
                                                        .atStartOfDay(ZoneId.systemDefault())
                                                        .toInstant()));
                       return event;
                   })
                   .doOnNext(calendar::addEvent)
                   .then()
                   .thenReturn(calendar);
    }
}
