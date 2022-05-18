package fr.terrier.apiterriercrm.properties;

import com.squareup.square.Environment;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "payments.square")
public class PaymentProperties {
    private String accessToken;
    private Environment environment;
    private String currency;
}
