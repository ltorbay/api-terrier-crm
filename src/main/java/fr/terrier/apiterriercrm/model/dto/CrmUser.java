package fr.terrier.apiterriercrm.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrmUser extends User {
    @NotBlank
    private String crmId;
}
