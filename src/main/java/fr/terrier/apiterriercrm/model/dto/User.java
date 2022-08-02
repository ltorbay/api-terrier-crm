package fr.terrier.apiterriercrm.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class User {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @Email
    @NotBlank
    private String email;
    private String phoneNumber;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    public String prettyPrint(final DateTimeFormatter dateFormat) {
        return String.format("""
                                     %s %s %s
                                     Contact : %s  %s""",
                             firstName, lastName, birthDate != null ? "Né(e) le " + birthDate.format(dateFormat) : "",
                             email, phoneNumber != null ? phoneNumber : "");
    }
}
