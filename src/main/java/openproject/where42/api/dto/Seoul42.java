package openproject.where42.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Seoul42 {
    private String login;
    private String location;
    private Image image;
    @JsonProperty("active?")
    private boolean active;
}