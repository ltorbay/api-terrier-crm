package fr.terrier.apiterriercrm.configuration;

import fr.terrier.apiterriercrm.utils.LoggingUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
public class LoggingFilter implements WebFilter {

    @NotNull
    @Override
    public Mono<Void> filter(@NotNull ServerWebExchange exchange, @NotNull WebFilterChain chain) {
        var request = exchange.getRequest();
        return chain.filter(exchange)
                    .doOnEach(LoggingUtils.logOnEach(r -> log.info("{} - {} {}",
                                                                   exchange.getResponse().getStatusCode(),
                                                                   request.getMethod(),
                                                                   request.getURI())))
                    .contextWrite(Context.of(LoggingUtils.XRID_KEY, Optional.ofNullable(request.getHeaders().get("X-Request-ID"))
                                                                            .filter(h -> !h.isEmpty())
                                                                            .map(h -> h.get(0))
                                                                            .orElseGet(UUID.randomUUID()::toString)))
                    .contextWrite(Context.of(LoggingUtils.XSID_KEY, Optional.ofNullable(request.getHeaders().get("X-Session-ID"))
                                                                            .filter(h -> !h.isEmpty())
                                                                            .map(h -> h.get(0))
                                                                            .orElseGet(UUID.randomUUID()::toString)));
    }
}
