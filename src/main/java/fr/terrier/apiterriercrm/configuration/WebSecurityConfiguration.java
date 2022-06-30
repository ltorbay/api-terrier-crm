package fr.terrier.apiterriercrm.configuration;

import fr.terrier.apiterriercrm.properties.CorsProperties;
import fr.terrier.apiterriercrm.properties.UserCredentials;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class WebSecurityConfiguration {
    private final UserCredentials adminUsers;
    private final CorsProperties corsProperties;

    @Bean
    SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) {
        return http.csrf().disable()
                   .cors().configurationSource(corsConfigurationSource()).and()
                   .httpBasic().and()
                   .formLogin().disable()
                   .authorizeExchange(authorize -> authorize
                           .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                           .pathMatchers("/public/**").permitAll()
                           .pathMatchers("/admin/**").hasRole("ADMIN")
                           .anyExchange().denyAll())
                   .build();
    }

    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(corsProperties.getAllowedOrigins());
        configuration.addAllowedHeader("content-type");
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        return new MapReactiveUserDetailsService(adminUsers.getUsers()
                                                           .stream()
                                                           .map(credentials -> User.builder()
                                                                                   .username(credentials.getLogin())
                                                                                   .password(credentials.getPassword())
                                                                                   .roles("ADMIN")
                                                                                   .build())
                                                           .toList());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
