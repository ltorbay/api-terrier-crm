package fr.terrier.apiterriercrm.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.squareup.square.models.Invoice;
import fr.terrier.apiterriercrm.model.dto.BookingDetails;
import fr.terrier.apiterriercrm.model.dto.BookingRequest;
import fr.terrier.apiterriercrm.model.dto.PricingDetail;
import liquibase.repackaged.org.apache.commons.lang3.StringUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.StringJoiner;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    @Value("${discord.client.login}")
    private String discordClientLogin;
    @Value("${discord.client.password}")
    private String discordClientPassword;
    @Value("${discord.client.channel-id}")
    private Long discordChannelId;
    private final Random random = new Random();
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/uuuu");
    private final Scheduler notificationScheduler = Schedulers.newBoundedElastic(10, 20, "notificationScheduler");

    public void notifyBooking(final BookingRequest request, final BookingDetails details, final Invoice invoice) {
        final StringJoiner lines = new StringJoiner("\n");
        lines.add("-----------------------------------------------------");
        lines.add("Nouvelle réservation effectuée ! On a plein de sous !");
        lines.add("");

        // Booking details
        lines.add(String.format("Du %s au %s pour %s personnes",
                                request.getPeriod().getStart().format(dateFormat),
                                request.getPeriod().getEnd().format(dateFormat),
                                request.getInformation().getGuestsCount()));
        lines.add("");

        // Customer information
        lines.add(String.format("Client : %s", request.getUser().prettyPrint(dateFormat)));
        lines.add("");
        if (StringUtils.isNotBlank(request.getInformation().getComment())) {
            lines.add("Commentaire client :");
            lines.add(request.getInformation().getComment());
            lines.add("");
        }

        // Pricing
        details.getPricing().stream().map(detail -> detail.prettyPrint(dateFormat)).forEach(lines::add);
        lines.add(String.format("Frais de ménage %s€", details.getCleaningFeeCents() / 100));
        lines.add(String.format("Total %s€", details.getAmount() / 100));
        if (Boolean.TRUE.equals(details.getDownPayment())) {
            lines.add(String.format("Avec acompte %s€", details.getDownPaymentAmount() / 100));
        } else {
            lines.add("Payé en totalité à la réservation");
        }
        lines.add("");

        // CRM
        lines.add(String.format("facture n°%s, ID %s - Commande ID %s, ", invoice.getInvoiceNumber(), invoice.getId(), invoice.getOrderId()));

        sendMessage(lines.toString());
    }

    public void sendMessage(String content) {
        var client = WebClient.create("https://discord.com/api/v9");
        var nonce = random.nextInt(1000000);

        client.post()
              .uri("/auth/login")
              .contentType(MediaType.APPLICATION_JSON)
              .bodyValue(Map.of("login", discordClientLogin,
                                "password", discordClientPassword))
              .retrieve()
              .toEntity(LoginResponse.class)
              .mapNotNull(ResponseEntity::getBody)
              .map(LoginResponse::getToken)
              // TODO cache token
              .flatMap(token -> client.post()
                                      .uri(String.format("/channels/%s/messages", discordChannelId))
                                      .header("authorization", token)
                                      .contentType(MediaType.APPLICATION_JSON)
                                      .bodyValue(Map.of("content", content,
                                                        "nonce", nonce,
                                                        "tts", false))
                                      .retrieve()
                                      .toEntity(SendMessageResponse.class))
              .doOnError(e -> log.warn("Unable to send discord notification", e))
              .doOnNext(response -> Optional.ofNullable(response.getBody())
                                            .map(SendMessageResponse::getNonce)
                                            .ifPresentOrElse(n -> log.info("Successfully sent discord notification"),
                                                             () -> log.warn("Discord answered empty or invalid nonce")))
              .subscribeOn(notificationScheduler)
              .timeout(Duration.ofMinutes(1))
              .subscribe();
    }

    @Getter
    @Setter
    @Accessors(fluent = false)
    static class LoginResponse {
        @JsonProperty
        String token;
    }

    @Getter
    @Setter
    @Accessors(fluent = false)
    static class SendMessageResponse {
        @JsonProperty
        Integer nonce;
    }
}
