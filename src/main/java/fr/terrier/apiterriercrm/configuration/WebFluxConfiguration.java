package fr.terrier.apiterriercrm.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistry;
import org.springframework.lang.NonNull;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalQueries;
import java.util.Locale;

@Configuration
@EnableWebFlux
public class WebFluxConfiguration implements WebFluxConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(new Formatter<LocalDate>() {
            final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

            @Override
            public @NonNull String print(@NonNull LocalDate object, @NonNull Locale locale) throws DateTimeException {
                return formatter.localizedBy(locale).format(object);
            }

            @Override
            public @NonNull LocalDate parse(@NonNull String text, @NonNull Locale locale) throws DateTimeException {
                return formatter.localizedBy(locale).parse(text, TemporalQueries.localDate());
            }
        });
    }

}
