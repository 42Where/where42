package openproject.where42.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OAuthToken {
    private String access_token;
    private String token_type;
    private String refresh_token;
    private int expires_in;
    private String scope;
    private int created_at;
}
