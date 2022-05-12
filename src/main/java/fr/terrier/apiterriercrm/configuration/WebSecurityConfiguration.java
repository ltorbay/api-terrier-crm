package fr.terrier.apiterriercrm.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class WebSecurityConfiguration {
    @Bean
    SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) {
        return http.csrf().disable()
                   .authorizeExchange(authorize -> authorize
                           .pathMatchers("/public/**").permitAll()
                           .pathMatchers("/**").hasRole("ADMIN")
                           .anyExchange().denyAll())
                   .build();
    }

    @Bean
    // TODO implement security from users stored in database
    public MapReactiveUserDetailsService userDetailsService() {
        return new MapReactiveUserDetailsService(User.builder()
                                                     .username("username")
                                                     .password("password")
                                                     .roles("ADMIN")
                                                     .build());
    }
}
