package fr.terrier.apiterriercrm.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.MDC;
import reactor.core.publisher.Signal;

import java.util.function.Consumer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoggingUtils {
    public static final String XRID_KEY = "XRID";
    public static final String XSID_KEY = "XSID";

    public static <T> Consumer<Signal<T>> log(Consumer<T> logStatement) {
        return signal -> {
            if (!signal.isOnNext()) return;
            log(logStatement, signal);
        };
    }

    public static <T> Consumer<Signal<T>> logOnEach(Consumer<T> logStatement) {
        return signal -> log(logStatement, signal);
    }

    private static <T> void log(Consumer<T> logStatement, Signal<T> signal) {
        try {
            MDC.put(XRID_KEY, signal.getContextView().get(XRID_KEY));
            MDC.put(XSID_KEY, signal.getContextView().get(XSID_KEY));
            logStatement.accept(signal.get());
        } finally {
            MDC.remove(XRID_KEY);
            MDC.remove(XSID_KEY);
        }
    }

}
