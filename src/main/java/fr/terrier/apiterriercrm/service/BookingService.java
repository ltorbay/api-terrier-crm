package fr.terrier.apiterriercrm.service;

import fr.terrier.apiterriercrm.mapper.BookingMapper;
import fr.terrier.apiterriercrm.mapper.BookingPeriodMapper;
import fr.terrier.apiterriercrm.model.dto.BookingRequest;
import fr.terrier.apiterriercrm.model.dto.BookingResponse;
import fr.terrier.apiterriercrm.model.dto.PaymentRequest;
import fr.terrier.apiterriercrm.model.dto.PricingDetail;
import fr.terrier.apiterriercrm.model.entity.booking.BookingEntity;
import fr.terrier.apiterriercrm.model.entity.booking.BookingInformationEntity;
import fr.terrier.apiterriercrm.model.entity.booking.BookingPeriodEntity;
import fr.terrier.apiterriercrm.model.entity.booking.BookingPricingDetailEntity;
import fr.terrier.apiterriercrm.model.enums.BookingStatus;
import fr.terrier.apiterriercrm.model.exception.ResponseException;
import fr.terrier.apiterriercrm.repository.BookingPricingDetailRepository;
import fr.terrier.apiterriercrm.repository.BookingRepository;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;
import reactor.core.scheduler.Scheduler;

import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingService {
    private final PaymentService paymentService;
    private final UserService userService;
    private final PricingService pricingService;
    private final BookingMapper bookingMapper;
    private final BookingPeriodMapper bookingPeriodMapper;
    private final BookingRepository bookingRepository;
    private final BookingPricingDetailRepository bookingPricingDetailRepository;
    @Qualifier("datasourceScheduler")
    private final Scheduler datasourceScheduler;

    public Mono<BookingResponse> book(@Valid @RequestBody BookingRequest bookingRequest) {
        // TODO map and handle booking errors to ResponseException
        // TODO check that period is not already booked (availability service ?)
        return pricingService.getBookingPriceDetail(bookingRequest.getType(), bookingRequest.getPeriod())
                             .handle((PricingDetail pricingDetail, SynchronousSink<PaymentRequest> sink) -> {
                                 var amount = pricingDetail.totalCents(bookingRequest.getType());
                                 if (!Objects.equals(amount, bookingRequest.getInformation().getPaymentAmountCents())) {
                                     sink.error(new ResponseException(HttpStatus.BAD_REQUEST, "Calculated booking amount %d and amount sent by client %d do not match", amount, bookingRequest.getInformation().getPaymentAmountCents()));
                                 } else {
                                     sink.next(PaymentRequest.builder()
                                                             .idempotencyKey(UUID.randomUUID())
                                                             .sourceId(bookingRequest.getInformation().getPaymentSourceId())
                                                             .amount(amount)
                                                             .detail(pricingDetail)
                                                             .build());
                                 }
                             })
                             .flatMap(paymentRequest -> userService.createOrGet(bookingRequest.getUser())
                                                                   .map(userEntity -> BookingEntity.builder()
                                                                                                   .period(BookingPeriodEntity.builder()
                                                                                                                              .start(bookingRequest.getPeriod().getStart())
                                                                                                                              .end(bookingRequest.getPeriod().getEnd())
                                                                                                                              .build())
                                                                                                   .information(BookingInformationEntity.builder()
                                                                                                                                        .comment(bookingRequest.getInformation().getComment())
                                                                                                                                        .guestsCount(bookingRequest.getInformation().getGuestsCount())
                                                                                                                                        .build())
                                                                                                   .type(bookingRequest.getType())
                                                                                                   .idempotencyKey(paymentRequest.getIdempotencyKey().toString())
                                                                                                   .userId(userEntity.getId())
                                                                                                   .build())
                                                                   .map(bookingRepository::save)
                                                                   .flatMap(bookingEntity -> persistPricingDetails(bookingEntity, paymentRequest.getDetail()).thenReturn(bookingEntity))
                                                                   .subscribeOn(datasourceScheduler)
                                                                   .flatMap(bookingEntity -> paymentService.createPayment(paymentRequest, bookingRequest.getUser().getEmail(), bookingEntity.getUserId())
                                                                                                           .flatMap(paymentResponse -> completeBooking(bookingEntity.getId(), paymentResponse.getPayment().getId()).thenReturn(bookingEntity))
                                                                                                           .doOnError(e -> log.error("Error while creating payment for booking completion", e))
                                                                                                           .onErrorResume(e -> abortBooking(bookingRequest).thenReturn(bookingEntity))))
                             .map(bookingMapper::map);
    }

    private Mono<Iterable<BookingPricingDetailEntity>> persistPricingDetails(final BookingEntity bookingEntity, final PricingDetail pricingDetail) {
        // noinspection BlockingMethodInNonBlockingContext
        return Mono.fromCallable(() -> pricingDetail.getPeriods()
                                                    .keySet()
                                                    .stream()
                                                    .map(bookingPeriod -> BookingPricingDetailEntity.builder()
                                                                                                    .bookingId(bookingEntity.getId())
                                                                                                    .bookingPeriod(bookingEntity.getPeriod())
                                                                                                    .periodConfiguration(bookingPeriodMapper.map(bookingPeriod))
                                                                                                    .build())
                                                    .toList())
                   .flatMap(details -> Mono.fromCallable(() -> bookingPricingDetailRepository.saveAll(details))
                                           .subscribeOn(datasourceScheduler));
    }

    private Mono<Boolean> completeBooking(final Long bookingId, @NonNull final String paymentId) {
        // noinspection BlockingMethodInNonBlockingContext
        return Mono.fromCallable(() -> bookingRepository.persistBookingPayment(BookingStatus.PAID, paymentId, bookingId))
                   .filter(success -> success)
                   .switchIfEmpty(Mono.error(() -> new ResponseException(HttpStatus.NOT_FOUND, "Unable to find existing booking for completion status update")))
                   .subscribeOn(datasourceScheduler);
    }

    private Mono<BookingEntity> abortBooking(final BookingRequest bookingRequest) {
        // TODO return ResponseException after abort ?
        // TODO implement abort
        return Mono.just(bookingMapper.map(bookingRequest));
    }
}

