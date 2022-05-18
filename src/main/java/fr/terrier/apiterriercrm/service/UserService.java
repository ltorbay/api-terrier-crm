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
    @Qualifier("datasourceScheduler")
    private final Scheduler datasourceScheduler;
    private final UserRepository userRepository;

    public Mono<UserEntity> createOrGet(User user) {
        // noinspection BlockingMethodInNonBlockingContext
        return Mono.fromCallable(() -> userRepository.findByEmail(user.email()))
                   .subscribeOn(datasourceScheduler)
                   .filter(Optional::isPresent)
                   .map(Optional::get)
                   .switchIfEmpty(Mono.fromCallable(() -> userRepository.save(new UserEntity().firstName(user.firstName())
                                                                                              .lastName(user.lastName())
                                                                                              .email(user.email())
                                                                                              .birthDate(user.birthDate())
                                                                                              .phoneNumber(user.phoneNumber()))))
                   .subscribeOn(datasourceScheduler);
    }
}
