package fr.terrier.apiterriercrm.service;

import fr.terrier.apiterriercrm.mapper.BookedPeriodMapper;
import fr.terrier.apiterriercrm.mapper.BookingMapper;
import fr.terrier.apiterriercrm.mapper.BookingPeriodMapper;
import fr.terrier.apiterriercrm.model.dto.BookedDates;
import fr.terrier.apiterriercrm.model.dto.BookingDetail;
import fr.terrier.apiterriercrm.model.dto.BookingDetails;
import fr.terrier.apiterriercrm.model.dto.BookingPricingCalculation;
import fr.terrier.apiterriercrm.model.dto.BookingRequest;
import fr.terrier.apiterriercrm.model.dto.BookingResponse;
import fr.terrier.apiterriercrm.model.dto.PricingDetail;
import fr.terrier.apiterriercrm.model.entity.booking.BookingEntity;
import fr.terrier.apiterriercrm.model.entity.booking.BookingInformationEntity;
import fr.terrier.apiterriercrm.model.entity.booking.BookingPeriodEntity;
import fr.terrier.apiterriercrm.model.entity.booking.BookingPricingDetailEntity;
import fr.terrier.apiterriercrm.model.enums.BookingStatus;
import fr.terrier.apiterriercrm.model.enums.BookingType;
import fr.terrier.apiterriercrm.model.exception.BookingException;
import fr.terrier.apiterriercrm.model.exception.ResponseException;
import fr.terrier.apiterriercrm.properties.PaymentProperties;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;
import reactor.core.scheduler.Scheduler;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {
    private final CrmService crmService;
    private final UserService userService;
    private final PricingService pricingService;
    private final NotificationService notificationService;
    private final BookingMapper bookingMapper;
    private final BookingPeriodMapper bookingPeriodMapper;
    private final BookedPeriodMapper bookedPeriodMapper;
    private final BookingRepository bookingRepository;
    private final BookingPricingDetailRepository bookingPricingDetailRepository;
    private final PaymentProperties paymentProperties;
    @Qualifier("datasourceScheduler")
    private final Scheduler datasourceScheduler;

    public Mono<BookingPricingCalculation> getBookingPriceDetails(final BookingType type, final LocalDate start, LocalDate end) {
        return pricingService.getBookingPriceDetails(type, start, end)
                             .map(details -> new BookingPricingCalculation(details, paymentProperties.getCleaningFeeCents(), paymentProperties.getDownPaymentRatio()));
    }

    public Mono<BookingResponse> book(@Valid @RequestBody BookingRequest bookingRequest) {
        if (Boolean.TRUE.equals(bookingRequest.getInformation().getDownPayment())
                && bookingRequest.getPeriod().getStart().isBefore(LocalDate.now().minusDays(paymentProperties.getDueDateMinDelayDays()))) {
            return Mono.error(() -> new BookingException("User requested down payment less than %s days before stay date (%s)",
                                                         paymentProperties.getDueDateMinDelayDays(),
                                                         bookingRequest.getPeriod().getStart().toString()));
        }

        return pricingService.getBookingPriceDetails(bookingRequest.getType(), bookingRequest.getPeriod().getStart(), bookingRequest.getPeriod().getEnd())
                             .handle((List<PricingDetail> pricingDetails, SynchronousSink<BookingDetails> sink) -> {
                                 var pricingCalculation = new BookingPricingCalculation(pricingDetails, paymentProperties.getCleaningFeeCents(), paymentProperties.getDownPaymentRatio());
                                 if (Boolean.TRUE.equals(bookingRequest.getInformation().getDownPayment())) {
                                     if (!Objects.equals(pricingCalculation.getDownPaymentTotalCents(), bookingRequest.getInformation().getPaymentAmountCents())) {
                                         sink.error(new ResponseException(HttpStatus.BAD_REQUEST, "Calculated down payment amount %d and amount sent by client %d do not match",
                                                                          pricingCalculation.getDownPaymentTotalCents(),
                                                                          bookingRequest.getInformation().getPaymentAmountCents()));
                                         return;
                                     }
                                 } else {
                                     if (!Objects.equals(pricingCalculation.getTotalCents(), bookingRequest.getInformation().getPaymentAmountCents())) {
                                         sink.error(new ResponseException(HttpStatus.BAD_REQUEST, "Calculated booking amount %d and amount sent by client %d do not match",
                                                                          pricingCalculation.getTotalCents(),
                                                                          bookingRequest.getInformation().getPaymentAmountCents()));
                                         return;
                                     }
                                 }

                                 sink.next(BookingDetails.builder()
                                                         .sourceId(bookingRequest.getInformation().getPaymentSourceId())
                                                         .amount(pricingCalculation.getTotalCents())
                                                         .pricing(pricingDetails)
                                                         .cleaningFeeCents(pricingCalculation.getCleaningFeeCents())
                                                         .downPayment(bookingRequest.getInformation().getDownPayment())
                                                         .downPaymentAmount(pricingCalculation.getDownPaymentTotalCents())
                                                         .build());
                             })
                             .flatMap(bookingDetails -> userService
                                     .createOrGet(bookingRequest.getUser())
                                     .flatMap(user -> Mono.just(user)
                                                          .map(userEntity -> BookingEntity.builder()
                                                                                          .period(BookingPeriodEntity.builder()
                                                                                                                     .start(bookingRequest.getPeriod().getStart())
                                                                                                                     .end(bookingRequest.getPeriod().getEnd())
                                                                                                                     .build())
                                                                                          .information(BookingInformationEntity.builder()
                                                                                                                               .comment(bookingRequest.getInformation().getComment())
                                                                                                                               .guestsCount(bookingRequest.getInformation().getGuestsCount())
                                                                                                                               .paymentAmountCents(bookingRequest.getInformation().getPaymentAmountCents())
                                                                                                                               .paymentSourceId(bookingRequest.getInformation().getPaymentSourceId())
                                                                                                                               .downPayment(bookingRequest.getInformation().getDownPayment())
                                                                                                                               .cleaningFeeCents(bookingRequest.getInformation().getCleaningFeeCents())
                                                                                                                               .build())
                                                                                          .type(bookingRequest.getType())
                                                                                          .userId(userEntity.getId())
                                                                                          .build())
                                                          .map(bookingRepository::save)
                                                          .flatMap(bookingEntity -> persistPricingDetails(bookingEntity, bookingDetails.getPricing()).thenReturn(bookingEntity))
                                                          .subscribeOn(datasourceScheduler)
                                                          .flatMap(bookingEntity -> crmService.createCard(bookingDetails, user)
                                                                                              .flatMap(card -> crmService.createInvoice(bookingDetails, card, user.getCrmId()))
                                                                                              .onErrorResume(e -> abortBooking(bookingEntity.getId()).then(Mono.error(new BookingException("Invoice generation failed", e))))
                                                                                              .doOnNext(invoice -> notificationService.notifyBooking(bookingRequest, bookingDetails, invoice))
                                                                                              .flatMap(invoice -> completeBooking(bookingEntity.getId(), invoice.getId()).thenReturn(bookingEntity))))
                                     .doOnError(e -> {
                                         log.error("Error while performing booking completion", e);
                                         notificationService.sendMessage("Error while performing booking completion\n\n" + e.toString());
                                     })
                                     .onErrorMap(e -> !(e instanceof ResponseException), e -> new BookingException("Error while performing booking completion", e))
                                     .map(bookingMapper::entityToResponse));
    }

    public Mono<BookedDates> getBookedDates(final LocalDate start, final LocalDate end) {
        // noinspection BlockingMethodInNonBlockingContext
        return Mono.fromCallable(() -> bookingRepository.findPeriodViewByPeriodBetween(start, end))
                   .map(bookedPeriodMapper::map)
                   .subscribeOn(datasourceScheduler);
    }

    public Flux<BookingDetail> getBookingDetail(final LocalDate start, final LocalDate end) {
        // noinspection BlockingMethodInNonBlockingContext
        return Mono.fromCallable(() -> bookingRepository.findByPeriodBetween(start, end))
                   .subscribeOn(datasourceScheduler)
                   .flatMapIterable(set -> set)
                   .map(bookingMapper::entityToDetail);
    }

    private Mono<Iterable<BookingPricingDetailEntity>> persistPricingDetails(final BookingEntity bookingEntity, final List<PricingDetail> pricingDetails) {
        // noinspection BlockingMethodInNonBlockingContext
        return Mono.fromCallable(() -> pricingDetails.stream()
                                                     .map(detail -> BookingPricingDetailEntity.builder()
                                                                                              .bookingId(bookingEntity.getId())
                                                                                              .bookingPeriod(bookingEntity.getPeriod())
                                                                                              .periodConfiguration(bookingPeriodMapper.map(detail.getPeriodConfiguration()))
                                                                                              .build())
                                                     .toList())
                   .flatMap(details -> Mono.fromCallable(() -> bookingPricingDetailRepository.saveAll(details))
                                           .subscribeOn(datasourceScheduler));
    }

    private Mono<Boolean> completeBooking(@NonNull final Long bookingId,
                                          @NonNull final String invoiceId) {
        // noinspection BlockingMethodInNonBlockingContext
        return Mono.fromCallable(() -> bookingRepository.persistBookingPayment(BookingStatus.PAID, invoiceId, bookingId))
                   .filter(success -> success)
                   .switchIfEmpty(Mono.error(() -> new ResponseException(HttpStatus.NOT_FOUND, "Unable to find existing booking for completion status update")))
                   .subscribeOn(datasourceScheduler);
    }

    private Mono<Boolean> abortBooking(@NonNull final Long bookingId) {
        // noinspection BlockingMethodInNonBlockingContext
        return Mono.fromCallable(() -> bookingRepository.persistBookingPayment(BookingStatus.CANCELLED, null, bookingId))
                   .filter(success -> success)
                   .switchIfEmpty(Mono.error(() -> new ResponseException(HttpStatus.NOT_FOUND, "Unable to find existing booking for completion status update")))
                   .subscribeOn(datasourceScheduler);
    }
}

