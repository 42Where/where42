package openproject.where42.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openproject.where42.api.ApiService;
import openproject.where42.api.mapper.OAuthToken;
import openproject.where42.api.mapper.Seoul42;
import openproject.where42.exception.customException.TooManyRequestException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminApiService {
    private final ApiService apiService;
    HttpEntity<MultiValueMap<String, String>> req;
    ResponseEntity<String> res;
    HttpHeaders headers;
    MultiValueMap<String, String> params;

    /**
     * <pre>
     *     42 call back code를 활용하여 신규 admin 42 OAuth Token 발행
     *     42api TooManyRequest 등 오류 발생 시 1초 간격으로 최대 3번 재시도
     *     api에서 주요하게 발생하는 exception은 "401 unauthorized"와 "429 too many request" 이나,
     *     이를 구별하여 exception 처리를 하지 않고 전부 429로 프론트에 throw 하고 있음
     *     따라서 실제 예외 발생 시 fallback 메소드에서 출력되는 e.message 로를 확인해야 함
     *     이는 401의 경우 secret_id 또는 token 만료로 인해 발생하는 것으로 휴먼 에러일 경우가 크며,
     *     그 외 1초에 2회 또는 10분에 1,200회 이상의 요청이 발생하는 경우 생기는 429 에러일 확률이 높기 때문임.
     *     따라서 접속량이 많지 않은 경우에 해당 메소드에서 에러 발생 시 상기 휴먼에러를 체크해야 함
     *     Retryable 어노테이션이 있는 모든 메소드에 해당됨
     * </pre>
     * @param secret 42api 호출을 위한 application secret_id
     * @param code 42api 호출을 위한 call back code
     * @return 발행된 OAuth Token
     * @throws TooManyRequestException 42api 에러 발생 시 429 예외 throw
     * @see #req42AdminHeader(String, String) 42api 토큰 요청 http 헤더 생성
     * @see ApiService#resPostApi(HttpEntity, URI) 외부 api post 요청 및 응답 반환
     * @see ApiService#oAuthTokenMapping(String) 외부 api 요청 응답 oAuthToken 매핑
     * @since 1.0
     * @author hyunjcho
     */
    @Retryable(maxAttempts = 3, backoff = @Backoff(1000))
    public OAuthToken getAdminOAuthToken(String secret, String code) {
        req = req42AdminHeader(secret, code);
        res = apiService.resPostApi(req, apiService.req42TokenUri());
        return apiService.oAuthTokenMapping(res.getBody());
    }

    /**
     * 기존 OAuth Token 만료 시 refresh token을 활용하여 신규 42 OAuth Token 발행
     * @param secret 42api 호출을 위한 application secret_id
     * @param refreshToken 신규 토큰 발행을 위한 Refresh Token
     * @return 발행된 신규 OAuth Token
     * @throws TooManyRequestException 42api 에러 발생 시 429 예외 throw
     * @see #req42AdminRefreshHeader(String, String) refresh token을 활용한 42api 토큰 요청 http 헤더 생성
     * @see ApiService#resPostApi(HttpEntity, URI) 외부 api post 요청 및 응답 반환
     * @see ApiService#oAuthTokenMapping(String) 외부 api 요청 응답 oAuthToken 매핑
     * @since 1.0
     * @author hyunjcho
     */
    @Retryable(maxAttempts = 3, backoff = @Backoff(1000))
    public OAuthToken getAdminNewOAuthToken(String secret, String refreshToken) {
        req = req42AdminRefreshHeader(secret, refreshToken);
        res = apiService.resPostApi(req, apiService.req42TokenUri());
        return apiService.oAuthTokenMapping(res.getBody());
    }

    /**
     * 42api 중 관리자용 특정 카뎃(name)의 "Me" 정보 호출
     * @param token 42api 호출용 OAuth Token
     * @param name me 정보를 호출 할 카뎃
     * @return Seoul42 반환
     * @throws TooManyRequestException 42api 에러 발생 시 429 예외 throw
     * @see Seoul42
     * @see ApiService#req42ApiHeader(String) 42api 요청 헤더 생성
     * @see ApiService#resReqApi(HttpEntity, URI) 외부 api requst 요청 및 응답 반환
     * @see ApiService#seoul42Mapping(String) 외부 api 요청 응답 Seoul42 매핑
     * @since 1.0
     * @author sunghkim
     */
    @Retryable(maxAttempts = 3, backoff = @Backoff(1000))
    public Seoul42 adminGetUserInfo(String token, String name) {
        req = apiService.req42ApiHeader(token);
        res = apiService.resReqApi(req, apiService.req42UserUri(name));
        return apiService.seoul42Mapping(res.getBody());
    }

    /**
     * 42api call back code를 통한 OAuth Token 요청 헤더 생성
     * @param secret 42api 호출을 위한 application secret_id
     * @param code 42api 호출을 위한 call back code
     * @return HttpEntity
     * @since 1.0
     * @author hyunjcho
     */
    public HttpEntity<MultiValueMap<String, String>> req42AdminHeader(String secret, String code) {
        headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        params = new LinkedMultiValueMap<>();
        params.add("grant_type","authorization_code");
        params.add("client_id","u-s4t2ud-99d32c302a658c39547534d834fffb48ffc5335d1d624127f4a1154a452d6eb1");
        params.add("client_secret", secret);
        params.add("code", code);
        params.add("redirect_uri","https://www.where42.kr/v2/auth/admin/callback");
        return new HttpEntity<>(params, headers);
    }

    /**
     * 42api Refresh Token을 통한 OAuth Token 요청 헤더 생성
     * @param secret 42api 호출을 위한 application secret_id
     * @param refreshToken 42api 호출을 위한 Refresh Token
     * @return HttpEntity
     * @since 1.0
     * @author hyunjcho
     */
    public HttpEntity<MultiValueMap<String, String>> req42AdminRefreshHeader(String secret, String refreshToken) {
        headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("client_id", "u-s4t2ud-99d32c302a658c39547534d834fffb48ffc5335d1d624127f4a1154a452d6eb1");
        params.add("client_secret", secret);
        params.add("refresh_token", refreshToken);
        return new HttpEntity<>(params, headers);
    }

    /**
     * <pre>
     *     상기의 메소드들이 3번 실패 시 실행되는 메서드
     *     실제 익셉션은 e.getMessage 메소드를 통해 로그로 남기고
     *     throw는 TooManyRequestException으로 통일
     * </pre>
     * @param e 던져진 e
     * @throws TooManyRequestException 42api 에러 발생 시 429 예외 throw
     * @since 1.0
     * @author hyunjcho
     */
    @Recover
    public Seoul42 fallback(RuntimeException e, String token) {
        log.info("[ApiService] {}", e.getMessage());
        throw new TooManyRequestException();
    }
}