package fr.terrier.apiterriercrm.service;

import com.squareup.square.SquareClient;
import com.squareup.square.exceptions.ApiException;
import com.squareup.square.models.CompletePaymentRequest;
import com.squareup.square.models.CompletePaymentResponse;
import com.squareup.square.models.CreateCardRequest;
import com.squareup.square.models.CreateCardResponse;
import com.squareup.square.models.CreateCustomerRequest;
import com.squareup.square.models.CreateCustomerResponse;
import com.squareup.square.models.CreateInvoiceRequest;
import com.squareup.square.models.CreateInvoiceResponse;
import com.squareup.square.models.CreateOrderRequest;
import com.squareup.square.models.CreateOrderResponse;
import com.squareup.square.models.CreatePaymentRequest;
import com.squareup.square.models.CreatePaymentResponse;
import com.squareup.square.models.GetInvoiceResponse;
import fr.terrier.apiterriercrm.model.exception.BookingException;
import fr.terrier.apiterriercrm.model.exception.InternalServerException;
import fr.terrier.apiterriercrm.model.exception.ResponseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrmClientProxy {
    private final SquareClient squareClient;

    @Qualifier("paymentScheduler")
    private final Scheduler paymentScheduler;

    public Mono<CreateCustomerResponse> createCustomer(final CreateCustomerRequest request) {
        return proxyClient("create customer",
                           () -> squareClient.getCustomersApi().createCustomerAsync(request));
    }

    public Mono<CreateOrderResponse> createOrder(final CreateOrderRequest request) {
        return proxyClient("create order",
                           () -> squareClient.getOrdersApi().createOrderAsync(request));
    }

    public Mono<CreateInvoiceResponse> createInvoice(final CreateInvoiceRequest request) {
        return proxyClient("create invoice",
                           () -> squareClient.getInvoicesApi().createInvoiceAsync(request));
    }
    
    public Mono<GetInvoiceResponse> getInvoice(final String invoiceId) {
        return proxyClient("get invoice",
                           () -> squareClient.getInvoicesApi().getInvoiceAsync(invoiceId));
    }

    public Mono<CompletePaymentResponse> completePayment(final String paymentId, final CompletePaymentRequest request) {
        return proxyClient("complete payment",
                           () -> squareClient.getPaymentsApi().completePaymentAsync(paymentId, request));
    }

    public Mono<CreatePaymentResponse> createPayment(final CreatePaymentRequest request) {
        return proxyClient("create payment",
                           () -> squareClient.getPaymentsApi().createPaymentAsync(request));
    }

    public Mono<CreateCardResponse> createCard(final CreateCardRequest request) {
        return proxyClient("create card",
                           () -> squareClient.getCardsApi().createCardAsync(request));
    }

    private <T> Mono<T> proxyClient(final String requestLabel, final Supplier<CompletableFuture<T>> supplier) {
        return Mono.fromFuture(supplier.get())
                   .retryWhen(Retry.backoff(3, Duration.ofMillis(200))
                                   .doBeforeRetry(signal -> log.info(String.format("Failed %s. will retry %s/%s", requestLabel, signal.totalRetries() + 1, 3), signal.failure()))
                                   .onRetryExhaustedThrow((backoff, signal) -> signal.failure())
                                   .scheduler(paymentScheduler))
                   .onErrorMap(ApiException.class, e -> new BookingException(String.format("Payment client API exception encountered while performing %s: %s", requestLabel, e.getErrors()), e))
                   .onErrorMap(IOException.class, e -> new InternalServerException(String.format("IO exception encountered while performing %s", requestLabel), e))
                   .onErrorMap(e -> !(e instanceof ResponseException), e -> new InternalServerException(String.format("Unexpected exception encountered while performing %s", requestLabel), e));
    }
}
