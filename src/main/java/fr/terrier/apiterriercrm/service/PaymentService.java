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

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final SquareClient squareClient;
    private final PaymentProperties paymentProperties;

    public Mono<CreatePaymentResponse> createPayment(final PaymentRequest paymentRequest) {
        // TODO handle errors
        return Mono.fromFuture(squareClient.getPaymentsApi()
                                           // TODO missing properties
                                           .createPaymentAsync(new CreatePaymentRequest.Builder(paymentRequest.sourceId(), 
                                                                                                paymentRequest.idempotencyKey().toString(), 
                                                                                                new Money(paymentRequest.amount(), paymentProperties.getCurrency()))
                                                                       .build()));
    }
}
