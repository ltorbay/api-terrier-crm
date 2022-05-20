package fr.terrier.apiterriercrm.service;

import fr.terrier.apiterriercrm.model.dto.User;
import fr.terrier.apiterriercrm.model.entity.UserEntity;
import fr.terrier.apiterriercrm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    @Qualifier("datasourceScheduler")
    private final Scheduler datasourceScheduler;

    public Mono<UserEntity> createOrGet(User user) {
        // noinspection BlockingMethodInNonBlockingContext
        return Mono.fromCallable(() -> userRepository.findByEmail(user.getEmail()))
                   .subscribeOn(datasourceScheduler)
                   .filter(Optional::isPresent)
                   .map(Optional::get)
                   .switchIfEmpty(Mono.fromCallable(() -> userRepository.save(UserEntity.builder()
                                                                                        .firstName(user.getFirstName())
                                                                                        .lastName(user.getLastName())
                                                                                        .email(user.getEmail())
                                                                                        .birthDate(user.getBirthDate())
                                                                                        .phoneNumber(user.getPhoneNumber())
                                                                                        .build())))
                   .subscribeOn(datasourceScheduler);
    }
}
