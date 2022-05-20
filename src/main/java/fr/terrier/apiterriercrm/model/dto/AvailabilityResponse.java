package fr.terrier.apiterriercrm.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.TreeSet;

@Getter
@Setter
@Builder
public class AvailabilityResponse {
    @JsonProperty
    private TreeSet<LocalDate> pear;
    @JsonProperty
    private TreeSet<LocalDate> grapes;
}
