package fr.terrier.apiterriercrm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
public class ApiTerrierCrmApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiTerrierCrmApplication.class, args);
	}

}
