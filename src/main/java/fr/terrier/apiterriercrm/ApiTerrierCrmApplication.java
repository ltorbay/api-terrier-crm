package fr.terrier.apiterriercrm;

import fr.terrier.apiterriercrm.properties.PaymentProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(PaymentProperties.class)
public class ApiTerrierCrmApplication {
// TODO when idle warns : 
//  HikariPool-1 - Thread starvation or clock leap detected (housekeeper delta=17m21s529ms)

    public static void main(String[] args) {
        SpringApplication.run(ApiTerrierCrmApplication.class, args);
    }

}
