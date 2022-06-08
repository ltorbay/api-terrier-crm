package fr.terrier.apiterriercrm.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "admin")
public class UserCredentials {
    private List<Credentials> users;

    @Getter
    @Setter
    public static class Credentials {
        private String login;
        private String password;
    }
}
