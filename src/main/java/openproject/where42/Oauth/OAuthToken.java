package openproject.where42.Oauth;

import lombok.Data;

@Data
public class OAuthToken {
    static final public String tokenHane = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHRmdW5jIjoiV2hlcmU0MiIsImlhdCI6MTY2ODM5MTIwMCwiZXhwIjoxNjcwOTgzMjAwfQ.N7N3IqsQFwuz1MU0OHN27f_QIZ1XEwnEAYgp4Iadz18";
    private String access_token;
    private String token_type;
    private String refresh_token;
    private int expires_in;
    private String scope;
    private int created_at;
}
