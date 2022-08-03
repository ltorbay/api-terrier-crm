package fr.terrier.apiterriercrm.service;

import com.squareup.square.models.Card;
import com.squareup.square.models.CreateCardRequest;
import com.squareup.square.models.CreateCardResponse;
import com.squareup.square.models.CreateCustomerRequest;
import com.squareup.square.models.CreateCustomerResponse;
import com.squareup.square.models.CreateInvoiceRequest;
import com.squareup.square.models.CreateInvoiceResponse;
import com.squareup.square.models.CreateOrderRequest;
import com.squareup.square.models.CreateOrderResponse;
import com.squareup.square.models.Customer;
import com.squareup.square.models.Invoice;
import com.squareup.square.models.InvoiceAcceptedPaymentMethods;
import com.squareup.square.models.InvoicePaymentReminder;
import com.squareup.square.models.InvoicePaymentRequest;
import com.squareup.square.models.InvoiceRecipient;
import com.squareup.square.models.MeasurementUnit;
import com.squareup.square.models.Money;
import com.squareup.square.models.Order;
import com.squareup.square.models.OrderLineItem;
import com.squareup.square.models.OrderLineItemAppliedTax;
import com.squareup.square.models.OrderLineItemTax;
import com.squareup.square.models.OrderQuantityUnit;
import fr.terrier.apiterriercrm.model.dto.BookingDetails;
import fr.terrier.apiterriercrm.model.dto.BookingPeriod;
import fr.terrier.apiterriercrm.model.dto.PricingDetail;
import fr.terrier.apiterriercrm.model.dto.User;
import fr.terrier.apiterriercrm.model.entity.UserEntity;
import fr.terrier.apiterriercrm.model.enums.Locale;
import fr.terrier.apiterriercrm.model.exception.InternalServerException;
import fr.terrier.apiterriercrm.properties.PaymentProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrmService {
    public static final String CARD_ON_FILE = "CARD_ON_FILE";
    public static final String BALANCE = "BALANCE";
    public static final String DEPOSIT = "DEPOSIT";
    private final CrmClientProxy crmClient;
    private final PaymentProperties paymentProperties;

    public Mono<Card> createCard(final BookingDetails bookingDetails, final UserEntity user) {
        return crmClient.createCard(new CreateCardRequest.Builder(UUID.randomUUID().toString(),
                                                                  bookingDetails.getSourceId(),
                                                                  new Card.Builder()
                                                                          .customerId(user.getCrmId())
                                                                          .cardholderName(user.getLastName())
                                                                          .enabled(true)
                                                                          .build())
                                            .build())
                        .map(CreateCardResponse::getCard);
    }

    public Mono<Customer> createCustomer(final User user) {
        return crmClient.createCustomer(new CreateCustomerRequest.Builder()
                                                .emailAddress(user.getEmail())
                                                .givenName(user.getFirstName())
                                                .familyName(user.getLastName())
                                                .phoneNumber(user.getPhoneNumber())
                                                .idempotencyKey(UUID.randomUUID().toString())
                                                .build())
                        .map(CreateCustomerResponse::getCustomer);
    }

    public Mono<Invoice> createInvoice(final BookingDetails bookingDetails, final Card card, final String userCrmId) {
        return createOrder(bookingDetails, userCrmId)
                .flatMap(order -> crmClient
                        .createInvoice(new CreateInvoiceRequest.Builder(new Invoice.Builder()
                                                                                .orderId(order.getId())
                                                                                .primaryRecipient(new InvoiceRecipient.Builder()
                                                                                                          .customerId(userCrmId)
                                                                                                          .build())
                                                                                .acceptedPaymentMethods(new InvoiceAcceptedPaymentMethods(true, false, false))
                                                                                .paymentRequests(invoicePaymentRequests(bookingDetails, card))
                                                                                .deliveryMethod("EMAIL")
                                                                                .build())
                                               .idempotencyKey(UUID.randomUUID().toString())
                                               .build())
                        .map(CreateInvoiceResponse::getInvoice))
                .doOnNext(order -> log.info("Created order {} at {}", order.getId(), order.getCreatedAt()));
    }

    public Mono<Order> createOrder(final BookingDetails bookingDetails, final String userCrmId) {
        var tva = new OrderLineItemTax.Builder()
                .uid(UUID.randomUUID().toString())
                .name("TVA")
                .percentage(paymentProperties.getTvaPercentage().toString())
                .type("INCLUSIVE")
                .scope("LINE_ITEM")
                .build();
        return crmClient.createOrder(new CreateOrderRequest.Builder()
                                             .order(new Order.Builder(paymentProperties.getSquare().getLocationId())
                                                            .taxes(List.of(tva))
                                                            .customerId(userCrmId)
                                                            .lineItems(Stream.concat(bookingDetails.getPricing()
                                                                                                   .stream()
                                                                                                   .map(detail -> orderLineItem(detail, tva.getUid())),
                                                                                     Stream.of(new OrderLineItem.Builder("1")
                                                                                                       .basePriceMoney(money(bookingDetails.getCleaningFeeCents()))
                                                                                                       .name("Frais de ménage")
                                                                                                       .appliedTaxes(List.of(new OrderLineItemAppliedTax(tva.getUid(), UUID.randomUUID().toString(), null)))
                                                                                                       .build()))
                                                                             .toList())
                                                            .build())
                                             .idempotencyKey(UUID.randomUUID().toString())
                                             .build())
                        .map(CreateOrderResponse::getOrder)
                        .doOnNext(order -> log.info("Created order {} at {}", order.getId(), order.getCreatedAt()));
    }

    private List<InvoicePaymentRequest> invoicePaymentRequests(final BookingDetails bookingDetails, final Card card) {
        var downPaymentMoney = money(bookingDetails.getDownPaymentAmount());

        if (!Boolean.TRUE.equals(bookingDetails.getDownPayment())) {
            return List.of(new InvoicePaymentRequest.Builder()
                                   .automaticPaymentSource(CARD_ON_FILE)
                                   .cardId(card.getId())
                                   .requestType(BALANCE)
                                   .dueDate(LocalDate.now().toString())
                                   .build());
        }

        return List.of(new InvoicePaymentRequest.Builder()
                               .fixedAmountRequestedMoney(downPaymentMoney)
                               .automaticPaymentSource(CARD_ON_FILE)
                               .cardId(card.getId())
                               .requestType(DEPOSIT)
                               .dueDate(LocalDate.now().toString())
                               .build(),
                       new InvoicePaymentRequest.Builder()
                               .automaticPaymentSource(CARD_ON_FILE)
                               .cardId(card.getId())
                               .requestType(BALANCE)
                               .dueDate(bookingDetails.getPricing()
                                                      .stream()
                                                      .map(PricingDetail::getBookingPeriod)
                                                      .map(BookingPeriod::getStart)
                                                      .sorted()
                                                      .findFirst()
                                                      .map(date -> date.minusDays(paymentProperties.getDueDateMinDelayDays()))
                                                      .map(LocalDate::toString)
                                                      .orElseThrow(() -> new InternalServerException("Missing booking period dates")))
                               .reminders(List.of(new InvoicePaymentReminder.Builder()
                                                          .message(paymentProperties.getReminderMessage())
                                                          .relativeScheduledDays(paymentProperties.getReminderDays())
                                                          .build()))
                               .build());
    }

    private OrderLineItem orderLineItem(final PricingDetail pricingDetail, final String taxUid) {
        return new OrderLineItem.Builder(pricingDetail.getBookingPeriod().consecutiveNights().toString())
                .basePriceMoney(money(pricingDetail.getTotalCents() / pricingDetail.getBookingPeriod().consecutiveNights()))
                // FIXME Add locale to requests
                .name(String.format("%s - %s", pricingDetail.getType().getLabel(Locale.FR), pricingDetail.getPeriodConfiguration()
                                                                                                         .getPeriodType()
                                                                                                         .getLabel(Locale.FR)))
                .appliedTaxes(List.of(new OrderLineItemAppliedTax(taxUid, UUID.randomUUID().toString(), null)))
                .quantityUnit(new OrderQuantityUnit.Builder()
                                      .precision(0)
                                      .measurementUnit(new MeasurementUnit.Builder()
                                                               .timeUnit("GENERIC_DAY")
                                                               .build())
                                      .build())
                .build();
    }

    private Money money(final Long amount) {
        return new Money(amount, paymentProperties.getSquare().getCurrency());
    }
}
