package openproject.where42.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openproject.where42.api.mapper.*;
import openproject.where42.exception.customException.*;
import openproject.where42.member.entity.enums.Planet;
import openproject.where42.token.AES;
import openproject.where42.util.Define;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;

/**
 * 42 및 hane 등 외부 api 호출을 위한 서비스 클래스
 * @version 2.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApiService {
    private final AES aes = new AES();
    private final ObjectMapper om = new ObjectMapper();
    private final RestTemplate rt = new RestTemplate();
    public final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.of("UTC")));
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    HttpHeaders headers;
    HttpEntity<MultiValueMap<String, String>> req;
    MultiValueMap<String, String> params;
    ResponseEntity<String> res;

    /**
     * <pre>
     *     42 call back code를 활용하여 신규 42 OAuth Token 발행
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
     * @see #req42TokenHeader(String, String) 42api 토큰 요청 http 헤더 생성
     * @see #resPostApi(HttpEntity, URI) 외부 api post 요청 및 응답 반환
     * @see #oAuthTokenMapping(String) 외부 api 요청 응답 oAuthToken 매핑
     * @since 1.0
     * @author hyunjcho
     */
    @Retryable(maxAttempts = 3, backoff = @Backoff(1000))
    public OAuthToken getOAuthToken(String secret, String code) {
        req = req42TokenHeader(secret, code);
        res = resPostApi(req, req42TokenUri());
        return oAuthTokenMapping(res.getBody());
    }

    /**
     * 기존 OAuth Token 만료 시 refresh token을 활용하여 신규 42 OAuth Token 발행
     * @param secret 42api 호출을 위한 application secret_id
     * @param refreshToken 신규 토큰 발행을 위한 Refresh Token
     * @return 발행된 신규 OAuth Token
     * @throws TooManyRequestException 42api 에러 발생 시 429 예외 throw
     * @see #req42RefreshHeader(String, String) refresh token을 활용한 42api 토큰 요청 http 헤더 생성
     * @see #resPostApi(HttpEntity, URI) 외부 api post 요청 및 응답 반환
     * @see #oAuthTokenMapping(String) 외부 api 요청 응답 oAuthToken 매핑
     * @since 1.0
     * @author hyunjcho
     */
    @Retryable(maxAttempts = 3, backoff = @Backoff(1000))
    public OAuthToken getNewOAuthToken(String secret, String refreshToken) {
        req = req42RefreshHeader(secret, refreshToken);
        res = resPostApi(req, req42TokenUri());
        return oAuthTokenMapping(res.getBody());
    }

    /**
     * 전체 카뎃의 42api 중 "Me" 정보 호출
     * @param token 42api 호출용 OAuth Token
     * @param i api 호출 page 지정
     * @return Seoul42 list 반환
     * @throws TooManyRequestException 42api 에러 발생 시 429 예외 throw
     * @see Seoul42
     * @see #req42ApiHeader(String) 42api 요청 헤더 생성
     * @see #resReqApi(HttpEntity, URI) 외부 api requst 요청 및 응답 반환
     * @see #seoul42ListMapping(String) 외부 api 요청 응답 List/<Seoul42/> 매핑
     * @since 1.0
     * @author hyunjcho
     */
    @Retryable(maxAttempts = 3, backoff = @Backoff(1000))
    public List<Seoul42> get42Image(String token, int i) {
        req = req42ApiHeader(token);
        res = resReqApi(req, req42ApiImageUri(i));
        return seoul42ListMapping(res.getBody());
    }

    /**
     * 현재 클러스터 아이맥 로그인 카뎃 전체 호출
     * @param token 42api 호출용 OAuth Token
     * @param i api 호출 페이지 지정
     * @return Cluster list 반환
     * @throws TooManyRequestException 42api 에러 발생 시 429 예외 throw
     * @see Cluster
     * @see #req42ApiHeader(String) 42api 요청 헤더 생성
     * @see #resReqApi(HttpEntity, URI) 외부 api requst 요청 및 응답 반환
     * @see #clusterMapping(String) 외부 api 요청 응답 Cluster 매핑
     * @since 1.0
     * @author hyunjcho
     */
    @Retryable(maxAttempts = 3, backoff = @Backoff(1000))
    public List<Cluster> get42ClusterInfo(String token, int i) {
        req = req42ApiHeader(token);
        res = resReqApi(req, req42ApiLocationUri(i));
        return clusterMapping(res.getBody());
    }

    /**
     * 현재로부터 5분 내 클러스터 아이맥에서 로그아웃 한 카뎃 전체 호출
     * @param token 42api 호출용 OAuth Token
     * @param i api 호출 페이지 지정
     * @return Cluster list 반환
     * @throws TooManyRequestException 42api 에러 발생 시 429 예외 throw
     * @see Cluster
     * @see #req42ApiHeader(String) 42api 요청 헤더 생성
     * @see #resReqApi(HttpEntity, URI) 외부 api requst 요청 및 응답 반환
     * @see #clusterMapping(String) 외부 api 요청 응답 Cluster 매핑
     * @since 1.0
     * @author hyunjcho
     */
    @Retryable(maxAttempts = 3, backoff = @Backoff(1000))
    public List<Cluster> get42LocationEnd(String token, int i) {
        req = req42ApiHeader(token);
        res = resReqApi(req, req42ApiLocationEndUri(i));
        return clusterMapping(res.getBody());
    }

    /**
     * 현재로부터 5분 내 클러스터 아이맥에 로그인 한 카뎃 전체 호출
     * @param token 42api 호출용 OAuth Token
     * @param i api 호출 페이지 지정
     * @return Cluster list 반환
     * @throws TooManyRequestException 42api 에러 발생 시 429 예외 throw
     * @see Cluster
     * @see #req42ApiHeader(String) 42api 요청 헤더 생성
     * @see #resReqApi(HttpEntity, URI) 외부 api requst 요청 및 응답 반환
     * @see #clusterMapping(String) 외부 api 요청 응답 Cluster 매핑
     * @since 1.0
     * @author hyunjcho
     */
    @Retryable(maxAttempts = 3, backoff = @Backoff(1000))
    public List<Cluster> get42LocationBegin(String token, int i) {
        req = req42ApiHeader(token);
        res = resReqApi(req, req42ApiLocationBeginUri(i));
        return clusterMapping(res.getBody());
    }

    /**
     * 42api 중 멤버 본인의 "Me" 정보 호출
     * @param token 42api 호출용 OAuth Token
     * @return Seoul42 반환
     * @throws TooManyRequestException 42api 에러 발생 시 429 예외 throw
     * @see Seoul42
     * @see #req42ApiHeader(String) 42api 요청 헤더 생성
     * @see #resReqApi(HttpEntity, URI) 외부 api requst 요청 및 응답 반환
     * @see #seoul42Mapping(String) 외부 api 요청 응답 Seoul42 매핑
     * @since 1.0
     * @author hyunjcho
     */
    @Retryable(maxAttempts = 3, backoff = @Backoff(1000))
    public Seoul42 getMeInfo(String token) {
        req = req42ApiHeader(aes.decoding(token));
        res = resReqApi(req, req42MeUri());
        return seoul42Mapping(res.getBody());
    }

    /**
     * 42api 중 특정 카뎃(name)의 "Me" 정보 호출
     * @param token 42api 호출용 OAuth Token
     * @param name me 정보를 호출 할 카뎃
     * @return Seoul42 반환
     * @throws TooManyRequestException 42api 에러 발생 시 429 예외 throw
     * @see Seoul42
     * @see #req42ApiHeader(String) 42api 요청 헤더 생성
     * @see #resReqApi(HttpEntity, URI) 외부 api requst 요청 및 응답 반환
     * @see #seoul42Mapping(String) 외부 api 요청 응답 Seoul42 매핑
     * @since 1.0
     * @author sunghkim
     */
    @Retryable(maxAttempts = 3, backoff = @Backoff(1000))
    public Seoul42 getUserInfo(String token, String name) {
        req = req42ApiHeader(aes.decoding(token));
        res = resReqApi(req, req42UserUri(name));
        return seoul42Mapping(res.getBody());
    }

    /**
     * begin부터 시작하여 end까지의 intra id를 가진 카뎃 10명의 "Me" 정보 반환
     * @param token 42api 호출용 OAuth Token
     * @param begin 조회를 시작할 이름
     * @param end 조회를 마칠 이름
     * @return Seoul42 list 반환
     * @throws TooManyRequestException 42api 에러 발생 시 429 예외 throw
     * @see Seoul42
     * @see #req42ApiHeader(String) 42api 요청 헤더 생성
     * @see #resReqApi(HttpEntity, URI) 외부 api requst 요청 및 응답 반환
     * @see #seoul42ListMapping(String) 외부 api 요청 응답 List/<Seoul42/> 매핑
     * @since 1.0
     * @author hyunjcho
     */
    @Retryable(maxAttempts = 3, backoff = @Backoff(1000))
    public List<Seoul42> get42UsersInfoInRange(String token, String begin, String end) {
        req = req42ApiHeader(aes.decoding(token));
        res = resReqApi(req, req42ApiUsersInRangeUri(begin, end));
        return seoul42ListMapping(res.getBody());
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

    /**
     * <pre>
     *     hane api 호출하여 정보 파싱 후 반환
     *     hane api가 "GAEPO", "SEOCHO" 반환 시 해당 planet 반환
     *     "OUT" 반환 시 마지막 태그 시간이 60분 이전일 경우 rest, 이후일 경우 out 반환
     *     사용자의 카드 태깅 오류 등으로 오류 발생 시 error를 반환하여 hane를 타지 않고 42api 정보만 조회할 수 있도록 함
     *     simpleDateFormat 오류 발생 시 태깅 시간과 상관 없이 out 반환
     * </pre>
     * @param name 조회할 멤버 이름
     * @param token hane api 호출용 토큰
     * @return 파싱된 Planet
     * @see Planet
     * @see #reqHaneApiHeader(String) Hane 요청 헤더 생성
     * @see #resReqApi(HttpEntity, URI) 외부 api requst 요청 및 응답 반환
     * @see #haneMapping(String) 외부 api 요청 응답 hane 매핑
     * @since 1.0
     * @author hyunjcho
     */
    public Planet getHaneInfo(String name, String token) {
        req = reqHaneApiHeader(token);
        try {
            res = resReqApi(req, reqHaneApiUri(name));
            Hane hane = haneMapping(res.getBody());
            if (hane.getInoutState().equalsIgnoreCase("OUT")) {
                Date now = new Date();
                sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                try {
                    Date tagAt = sdf.parse(hane.getTag_at());
                    if ((now.getTime() - tagAt.getTime()) / 60000 < 60)
                        return Planet.rest;
                    return null;
                } catch (Exception e) {
                    log.info("[SimpleDateFormat Error] \"{}\"님의 태그 시간 계산 오류가 발생하였습니다. 카드 태깅시간과 관계 없이 퇴근으로 표기됩니다.", name);
                    return null;
                }
            }
            if (hane.getCluster().equalsIgnoreCase("GAEPO"))
                return Planet.gaepo;
            return Planet.seocho;
        } catch (RuntimeException e) {
            log.info("[Hane Error] \"{}\"님의 hane api 오류가 발생하였습니다. 발생 에러: {}", name, e.getMessage());
            return Planet.error;
        }
    }

    /**
     * 42api call back code를 통한 OAuth Token 요청 헤더 생성
     * @param secret 42api 호출을 위한 application secret_id
     * @param code 42api 호출을 위한 call back code
     * @return HttpEntity
     * @since 1.0
     * @author hyunjcho
     */
    public HttpEntity<MultiValueMap<String, String>> req42TokenHeader(String secret, String code) {
        headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        params = new LinkedMultiValueMap<>();
        params.add("grant_type","authorization_code");
        params.add("client_id","u-s4t2ud-96b21711604494be33aa7beca93431fd55840266ddc3b9dc906db4c5ca0fed32");
        params.add("client_secret", secret);
        params.add("code", code);
        params.add("redirect_uri","https://www.where42.kr/v2/auth/callback");
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
    public HttpEntity<MultiValueMap<String, String>> req42RefreshHeader(String secret, String refreshToken) {
        headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("client_id", "u-s4t2ud-96b21711604494be33aa7beca93431fd55840266ddc3b9dc906db4c5ca0fed32");
        params.add("client_secret", secret);
        params.add("refresh_token", refreshToken);
        return new HttpEntity<>(params, headers);
    }

    /**
     * 42api 요청 헤더 생성
     * @param token 42api 호출을 위한 OAuth Token
     * @return HttpEntity
     * @since 1.0
     * @author hyunjcho
     */
    public HttpEntity<MultiValueMap<String, String>> req42ApiHeader(String token) {
        headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        headers.add("Content-type", "application/json;charset=utf-8");
        params = new LinkedMultiValueMap<>();
        return new HttpEntity<>(params, headers);
    }

    /**
     * hane api 요청 헤더 생성
     * @param token hane api 호출을 위한 JWT Token
     * @return HttpEntity
     * @since 1.0
     * @author sunghkim
     */
    public HttpEntity<MultiValueMap<String, String>> reqHaneApiHeader(String token) {
        headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        headers.add("Content-type", "application/json;charset=utf-8");
        params = new LinkedMultiValueMap<>();
        return new HttpEntity<>(params, headers);
    }

    /**
     * OAuth Token 발행 요청 42api URI 생성
     * @return URI
     * @since 1.0
     * @author hyunjcho
     */
    public URI req42TokenUri() {
        return UriComponentsBuilder.fromHttpUrl("https://api.intra.42.fr/oauth/token")
                .build()
                .toUri();
    }

    /**
     * <pre>
     *     42seoul 캠퍼스 학생 전체 조회 42api URI 생성
     *     intraId 알파벳 순으로 정렬된 카뎃들을 한 번에 100명 호출하며,
     *     넘겨받은 i에 따라 다른 페이지를 조회함
     * </pre>
     * @param i 조회할 페이지
     * @return URI
     * @since 1.0
     * @author hyunjcho
     */
    public URI req42ApiImageUri(int i) {
        return UriComponentsBuilder.newInstance()
                .scheme("https").host("api.intra.42.fr").path(Define.INTRA_VERSION_PATH + "/campus/" + Define.SEOUL + "/users")
                .queryParam("sort", "login")
                .queryParam("filter[kind]", "student")
                .queryParam("page[size]", 100)
                .queryParam("page[number]", i)
                .build()
                .toUri();
    }

    /**
     * <pre>
     *      42seoul 캠퍼스에서 아이맥 로그인 정보 중 가장 최근의 end_at 시점부터 100명씩 조히하는 42api URI 생성
     *      end_at이 null일 경우 현재 해당 카뎃이 대상 아이맥을 점유하고 있는 상태임
     *      이를 통해 현재 클러스터 아이맥에 로그인 한 카뎃들의 정보를 가져올 수 있음
     *      ".queryParam("range[end_at]", null)"과 같이 조회가 가능할 경우 로그아웃 카뎃만 부를 수 있으나,
     *      현재는 방법을 찾지 못해 호출지에서 반환된 리스트의 마지막 카뎃이 null이 아닌 경우까지 호출하도록 하고 있음
     * </pre>
     * @param i 조회할 페이지
     * @return URI
     * @since 1.0
     * @author hyunjcho
     */
    public URI req42ApiLocationUri(int i) {
        return UriComponentsBuilder.newInstance()
                .scheme("https").host("api.intra.42.fr").path(Define.INTRA_VERSION_PATH + "/campus/" + Define.SEOUL + "/locations")
                .queryParam("page[size]", 100)
                .queryParam("page[number]", i)
                .queryParam("sort", "-end_at")
                .build()
                .toUri();
    }

    /**
     *  <pre>
     *      42seoul 캠퍼스에서 최근 5분 이내 end_at(로그아웃)이 갱신된 모든 경우에 대해 100건씩 조회하는 42api URI 생성
     *      해당 location 정보는 실시간으로 갱신되어 최신순으로 정렬되어 있음
     *      A 아이맥에서 로그아웃 후 바로 B 아이맥에서 로그인 시 end_at과 begin_at에 동시 조회되나
     *      해당 카뎃의 location 정보에는 조회 당시의 로그인 아이맥 정보가 담겨있으므로 정보는 누락되지 않음
     *  </pre>
     * @param i 조회할 페이지
     * @return URI
     * @since 1.0
     * @author hyunjcho
     */
    public URI req42ApiLocationEndUri(int i) {
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = new Date();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, -5);
        return UriComponentsBuilder.newInstance()
                .scheme("https").host("api.intra.42.fr").path(Define.INTRA_VERSION_PATH + "/campus/" + Define.SEOUL + "/locations")
                .queryParam("page[size]", 100)
                .queryParam("page[number]", i)
                .queryParam("range[end_at]", sdf.format(cal.getTime()) + "," + sdf.format(date))
                .build()
                .toUri();
    }

    /**
     *  <pre>
     *      42seoul 캠퍼스에서 최근 5분 이내 begin_at(로그인)이 갱신된 모든 경우에 대해 100건씩 조회하는 42api URI 생성
     *      해당 location 정보는 실시간으로 갱신되어 최신순으로 정렬되어 있음
     *      A 아이맥에서 로그아웃 후 바로 B 아이맥에서 로그인 시 end_at과 begin_at에 동시 조회되나
     *      해당 카뎃의 location 정보에는 조회 당시의 로그인 아이맥 정보가 담겨있으므로 정보는 누락되지 않음
     *  </pre>
     * @param i 조회할 페이지
     * @return URI
     * @since 1.0
     * @author hyunjcho
     */
    public URI req42ApiLocationBeginUri(int i) {
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = new Date();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, -5);
        return UriComponentsBuilder.newInstance()
                .scheme("https").host("api.intra.42.fr").path(Define.INTRA_VERSION_PATH + "/campus/" + Define.SEOUL + "/locations")
                .queryParam("page[size]", 50)
                .queryParam("page[number]", i)
                .queryParam("range[begin_at]", sdf.format(cal.getTime()) + "," + sdf.format(date))
                .build()
                .toUri();
    }

    /**
     * 멤버 본인의 me 정보 조회 42api URI 생성
     * @return URI
     * @since 1.0
     * @author hyunjcho
     */
    public URI req42MeUri() {
        return UriComponentsBuilder.newInstance()
                .scheme("https").host("api.intra.42.fr").path(Define.INTRA_VERSION_PATH + "/me")
                .build()
                .toUri();
    }

    /**
     * 전달받은 name 카뎃의 me 정보 조회 42api URI 생성
     * @param name
     * @return URI
     * @since 2.0
     * @author hyunjcho
     */
    public URI req42UserUri(String name) {
        return UriComponentsBuilder.newInstance()
                .scheme("https").host("api.intra.42.fr").path(Define.INTRA_VERSION_PATH + "/users/" + name)
                .build()
                .toUri();
    }

    /**
     * 전달받은 begin부터 end까지의 카뎃을 알파벳으로 정렬하여 10명씩 조회하는 42api URI 생성
     * @param begin 조회를 시작하고자 하는 단어
     * @param end 조회를 끝내고자 하는 단어
     * @return URI
     * @since 1.0
     * @author hyunjcho
     */
    public URI req42ApiUsersInRangeUri(String begin, String end) {
        return UriComponentsBuilder.newInstance()
                .scheme("https").host("api.intra.42.fr").path(Define.INTRA_VERSION_PATH + "/campus/" + Define.SEOUL + "/users")
                .queryParam("sort", "login")
                .queryParam("range[login]", begin + "," + end)
                .queryParam("page[size]", "10")
                .build()
                .toUri();
    }

    /**
     * hane api 호출 URI 생성"
     * @param name 조회하고자 하는 카뎃
     * @return URI
     * @since 1.0
     * @author sunghkim
     */
    public URI reqHaneApiUri(String name) {
        return UriComponentsBuilder.fromHttpUrl("hane url")
                .build()
                .toUri();
    }

    /**
     * 42api OAuth Token 요청에 대한 반환 json을 OAuthToken 클래스로 매핑
     * @param body
     * @return 반환된 정보가 매핑된 OAuthToken 클래스
     * @see OAuthToken
     * @since 1.0
     * @author hyunjcho
     */
    public OAuthToken oAuthTokenMapping(String body) {
        OAuthToken oAuthToken = null;
        try {
            oAuthToken = om.readValue(body, OAuthToken.class);
        } catch (JsonProcessingException e) {
            throw new JsonDeserializeException();
        }
        return oAuthToken;
    }

    /**
     * 42api 카뎃 정보 요청에 대한 반환 json을 Seoul42 클래스로 매핑
     * @param body 반환된 json body
     * @return 반환된 정보가 매핑된 Seoul42 클래스
     * @see Seoul42
     * @since 1.0
     * @author hyunjcho
     */
    public Seoul42 seoul42Mapping(String body) {
        Seoul42 seoul42 = null;
        try {
            seoul42 = om.readValue(body, Seoul42.class);
        } catch (JsonProcessingException e) {
            throw new JsonDeserializeException();
        }
        return seoul42;
    }

    /**
     * 42api 로케이션 정보 요청에 대한 반환 json을 Cluster 클래스로 매핑
     * @param body 반환된 json body
     * @return 반환된 정보가 매핑된 Cluster 클래스
     * @see Cluster
     * @since 1.0
     * @author hyunjcho
     */
    public List<Cluster> clusterMapping(String body) {
        List<Cluster> clusters = null;
        try {
            clusters = Arrays.asList(om.readValue(body, Cluster[].class));
        } catch (JsonProcessingException e) {
            throw new JsonDeserializeException();
        }
        return clusters;
    }

    /**
     * 42api 여러 카뎃 정보 요청에 대한 반환 json을 List/<Seoul42/> 클래스로 매핑
     * @param body 반환된 json body
     * @return 반환된 정보가 매핑된 List/<Seoul42/> 클래스
     * @see Seoul42
     * @since 1.0
     * @author hyunjcho
     */
    public List<Seoul42> seoul42ListMapping(String body) {
        List<Seoul42> seoul42List = null;
        try {
            seoul42List = Arrays.asList(om.readValue(body, Seoul42[].class));
        } catch (JsonProcessingException e) {
            throw new JsonDeserializeException();
        }
        return seoul42List;
    }

    /**
     * hane api 정보 요청에 대한 반환 json을 Hane 클래스로 매핑
     * @param body 반환된 json body
     * @return 반환된 정보가 매핑된 Hane 클래스
     * @see Hane
     * @since 1.0
     * @author hyunjcho
     */
    public Hane haneMapping(String body) {
        Hane hane = null;
        try {
            hane = om.readValue(body, Hane.class);
        } catch (JsonProcessingException e) {
            throw new JsonDeserializeException();
        }
        return hane;
    }


    /**
     * 외부 api request 요청에 대한 응답 반환
     * @param req 요청 헤더
     * @param url 요청 url
     * @return 요청에 대한 응답
     * @since 1.0
     * @author hyunjcho
     */
    public ResponseEntity<String> resReqApi(HttpEntity<MultiValueMap<String, String>> req, URI url) {
        return rt.exchange(
                url.toString(),
                HttpMethod.GET,
                req,
                String.class);
    }

    /**
     * 외부 api post 요청에 대한 응답 반환
     * @param req 요청 헤더
     * @param url 요청 url
     * @return 요청에 대한 응답
     * @since 1.0
     * @author hyunjcho
     */
    public ResponseEntity<String> resPostApi(HttpEntity<MultiValueMap<String, String>> req, URI url) {
        return rt.exchange(
                url.toString(),
                HttpMethod.POST,
                req,
                String.class);
    }
}
