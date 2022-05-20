package fr.terrier.apiterriercrm.configuration;

import com.squareup.square.SquareClient;
import fr.terrier.apiterriercrm.properties.PaymentProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class PaymentConfiguration {
    private final PaymentProperties paymentProperties;

    @Bean
    SquareClient squareClient() {
        return new SquareClient.Builder()
                .environment(paymentProperties.getEnvironment())
                .accessToken(paymentProperties.getAccessToken())
                .build();
    }
}
