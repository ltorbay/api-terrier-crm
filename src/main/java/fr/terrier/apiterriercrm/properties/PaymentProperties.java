package fr.terrier.apiterriercrm.properties;

import com.squareup.square.Environment;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "payments")
public class PaymentProperties {
    private Square square;
    private Double tvaPercentage;
    private Double downPaymentRatio;
    private Integer dueDateMinDelayDays;
    private Integer reminderDays;
    private String reminderMessage;

    @Getter
    @Setter
    public static class Square {
        private String accessToken;
        private Environment environment;
        private String currency;
        private String locationId;
    }
}
