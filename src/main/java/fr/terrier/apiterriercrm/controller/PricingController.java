package fr.terrier.apiterriercrm.controller;

import fr.terrier.apiterriercrm.model.dto.PeriodConfiguration;
import fr.terrier.apiterriercrm.service.PricingService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

@RestController
@RequestMapping("/public/pricing")
@RequiredArgsConstructor
public class PricingController {
    private final PricingService pricingService;

    @GetMapping
    public Mono<SortedSet<PeriodConfiguration>> getPricingConfiguration(@RequestParam @NotNull final LocalDate start,
                                                                        @RequestParam @NotNull final LocalDate end) {
        return pricingService.getPricingPattern(start, end)
                             .collect(() -> new TreeSet<>(Comparator.comparing(PeriodConfiguration::getStart)), Set::add);
    }
}
