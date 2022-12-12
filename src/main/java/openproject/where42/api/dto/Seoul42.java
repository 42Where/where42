package openproject.where42.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Seoul42 {
    private String login;
    private String location;
    private Image image;
    private String active;
}