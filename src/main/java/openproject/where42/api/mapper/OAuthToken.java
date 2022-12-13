package openproject.where42.api.mapper;

import lombok.Getter;
@Getter
public class OAuthToken {
    private String access_token;
    private String token_type;
    private String refresh_token;
    private int expires_in;
    private String scope;
    private int created_at;
}
