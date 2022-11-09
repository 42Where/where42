package openproject.where42.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Seoul42 { // 더 좋은 이름 없으려나.. 써치카뎃은 딱 와닿는데...
    String login;
    String location;
    String image_url;
}