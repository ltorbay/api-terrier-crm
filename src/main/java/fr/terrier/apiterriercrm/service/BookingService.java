package fr.terrier.apiterriercrm.service;

import fr.terrier.apiterriercrm.model.dto.BookingPeriod;
import fr.terrier.apiterriercrm.model.dto.BookingRequest;
import fr.terrier.apiterriercrm.model.dto.BookingResponse;
import fr.terrier.apiterriercrm.model.dto.PaymentRequest;
import fr.terrier.apiterriercrm.model.entity.BookingEntity;
import fr.terrier.apiterriercrm.model.entity.BookingInformationEntity;
import fr.terrier.apiterriercrm.model.entity.BookingPeriodEntity;
import fr.terrier.apiterriercrm.model.enums.BookingStatus;
import fr.terrier.apiterriercrm.model.enums.BookingType;
import fr.terrier.apiterriercrm.model.exception.ResponseException;
import fr.terrier.apiterriercrm.repository.BookingRepository;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;
import reactor.core.scheduler.Scheduler;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {
    @Qualifier("datasourceScheduler")
    private final Scheduler datasourceScheduler;
    private final PaymentService paymentService;
    private final UserService userService;
    private final BookingRepository bookingRepository;

    public Mono<BookingResponse> book(@Valid @RequestBody BookingRequest request) {
        // TODO map and handle booking errors to ResponseException
        return Mono.fromCallable(() -> computeAmountCents(request.type(), request.period()))
                   .handle((Long amount, SynchronousSink<PaymentRequest> sink) -> {
                       if (!Objects.equals(amount, request.information().paymentAmountCents())) {
                           sink.error(new ResponseException(HttpStatus.BAD_REQUEST, "Calculated booking amount and amount sent by client do not match"));
                       }
                       sink.next(new PaymentRequest().idempotencyKey(UUID.randomUUID())
                                                     .sourceId(request.information().paymentSourceId())
                                                     .amount(amount));
                   })
                   .flatMap(paymentRequest -> userService.createOrGet(request.user())
                                                         .map(userEntity -> new BookingEntity().period(new BookingPeriodEntity()
                                                                                                               .start(request.period().start())
                                                                                                               .end(request.period().end()))
                                                                                               .information(new BookingInformationEntity().comment(request.information().comment())
                                                                                                                                          .guestsCount(request.information().guestsCount()))
                                                                                               .type(request.type())
                                                                                               .idempotencyKey(paymentRequest.idempotencyKey())
                                                                                               .user(userEntity))
                                                         .map(bookingRepository::save)
                                                         .subscribeOn(datasourceScheduler)
                                                         .flatMap(booking -> paymentService.createPayment(paymentRequest)
                                                                                           .flatMap(paymentResponse -> completeBooking(booking.id(), paymentResponse.getPayment().getId()))))
                   .map(paymentResponse -> new BookingResponse().period(request.period()));
    }

    // TODO return data with computed full weeks / periods / additional fees and total price
    public Mono<BookingResponse> computeBooking(BookingType type, BookingPeriod period) {
        return Mono.fromCallable(() -> computeAmountCents(type, period))
                   .map(amount -> new BookingResponse().period(period));
    }

    private Long computeAmountCents(final BookingType type, final BookingPeriod period) {
        // TODO use external sevice calculating total price (full weeks, nights etc. using high/low season configurations)
        return 1000L;
    }

    private Mono<BookingEntity> completeBooking(final Long bookingId, @NonNull final String paymentId) {
        // noinspection BlockingMethodInNonBlockingContext
        return Mono.fromCallable(() -> bookingRepository.persistBookingPayment(BookingStatus.PAID, paymentId, bookingId))
                   .filter(Optional::isPresent)
                   .switchIfEmpty(Mono.error(() -> new ResponseException(HttpStatus.NOT_FOUND, "Unable to find existing booking for completion status update")))
                   .map(Optional::get)
                   .subscribeOn(datasourceScheduler);
    }

    private Mono<Void> abortBooking(final BookingRequest bookingRequest) {
        // TODO
        return Mono.empty();
    }
}
