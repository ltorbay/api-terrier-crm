package fr.terrier.apiterriercrm.model.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Persistent;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@Persistent
public class UserEntity {
    @Id
    private Long id;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    @Email
    private String email;

    private String phoneNumber;
    private LocalDate birthDate;
}
