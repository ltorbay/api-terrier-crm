package fr.terrier.apiterriercrm.service;

import com.squareup.square.SquareClient;
import com.squareup.square.models.CreatePaymentRequest;
import com.squareup.square.models.CreatePaymentResponse;
import com.squareup.square.models.Money;
import fr.terrier.apiterriercrm.model.dto.PaymentRequest;
import fr.terrier.apiterriercrm.properties.PaymentProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final SquareClient squareClient;
    private final PaymentProperties paymentProperties;

    public Mono<CreatePaymentResponse> createPayment(final PaymentRequest paymentRequest, final String buyerEmail, final Long userId) {
        // TODO handle errors
        return Mono.fromFuture(squareClient.getPaymentsApi()
                                           .createPaymentAsync(new CreatePaymentRequest.Builder(paymentRequest.getSourceId(), 
                                                                                                paymentRequest.getIdempotencyKey().toString(), 
                                                                                                new Money(paymentRequest.getAmount(), paymentProperties.getCurrency()))
                                                                       .buyerEmailAddress(buyerEmail)
                                                                       .customerId(userId.toString())
                                                                       .build()));
    }
}
