package openproject.where42.api.mapper;

import lombok.Getter;

/**
 * 42api 토큰 정보 매핑용 클래스
 * @version 1.0
 * @author hyunjcho
 */
@Getter
public class OAuthToken {
    private String access_token;
    private String token_type;
    private String refresh_token;
    private int expires_in;
    private String scope;
    private int created_at;
}
