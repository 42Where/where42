package openproject.where42.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import openproject.where42.api.mapper.*;
import openproject.where42.exception.customException.*;
import openproject.where42.token.AES;
import openproject.where42.member.entity.enums.Planet;
import openproject.where42.util.Define;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class ApiService {
    private static final AES aes = new AES();
    private static final ObjectMapper om = new ObjectMapper();
    private static final RestTemplate rt = new RestTemplate();
    public static final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.of("UTC")));
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    HttpHeaders headers;
    HttpEntity<MultiValueMap<String, String>> req;
    MultiValueMap<String, String> params;
    ResponseEntity<String> res;

    // oAuth 토큰 반환
    @Retryable(maxAttempts = 10, backoff = @Backoff(1000))
    @Async("apiTaskExecutor")
    public CompletableFuture<OAuthToken> getOAuthToken(String code) {
        req = req42TokenHeader(code);
        res = resPostApi(req, req42TokenUri());
        return CompletableFuture.completedFuture(oAuthTokenMapping(res.getBody()));
    }

    // new oAuth 토큰 반환
    @Retryable(maxAttempts = 10, backoff = @Backoff(1000))
    @Async("apiTaskExecutor")
    public CompletableFuture<OAuthToken> getNewOAuthToken(String token) {
        req = req42RefreshHeader(token);
        res = resPostApi(req, req42TokenUri());
        return CompletableFuture.completedFuture(oAuthTokenMapping(res.getBody()));
    }

    // 관리자 oAuth 토큰 반환
    public OAuthToken getAdminOAuthToken(String code) {
		req = req42AdminHeader(code);
        res = resPostApi(req, req42TokenUri());
        return oAuthTokenMapping(res.getBody());
    }

    // 관리자 new oAuth 토큰 반환
    public OAuthToken getAdminNewOAuthToken(String token) {
		req = req42AdminRefreshHeader(token);
        res = resPostApi(req, req42TokenUri());
        return oAuthTokenMapping(res.getBody());
    }

    // 이미지 호출
    @Retryable(maxAttempts = 10, backoff = @Backoff(1000))
    @Async("apiTaskExecutor")
    public CompletableFuture<List<Seoul42>> get42Image(String token, int i) {
        req = req42ApiHeader(token);
        res = resReqApi(req, req42ApiImageUri(i));
        return CompletableFuture.completedFuture(seoul42ListMapping(res.getBody()));
    }

    // 현재 클러스터에 있는 카뎃들 호출
    @Retryable(maxAttempts = 10, backoff = @Backoff(1000))
    @Async("apiTaskExecutor")
    public CompletableFuture<List<Cluster>> get42ClusterInfo(String token, int i) {
        req = req42ApiHeader(token);
        res = resReqApi(req, req42ApiLocationUri(i));
        return CompletableFuture.completedFuture(clusterMapping(res.getBody()));
    }

    // 로그아웃한 카뎃들 호출
    @Retryable(maxAttempts = 10, backoff = @Backoff(1000))
    public List<Cluster> get42LocationEnd(String token, int i) {
        req = req42ApiHeader(token);
        try {
            res = resReqApi(req, req42ApiLocationEndUri(i));
        } catch (RuntimeException e) {
            System.out.println("==== end error ====");
            e.printStackTrace();
        }
        return clusterMapping(res.getBody());
    }

    // 로그인한 카뎃들 호출
    @Retryable(maxAttempts = 10, backoff = @Backoff(1000))
    public List<Cluster> get42LocationBegin(String token, int i) {
        req = req42ApiHeader(token);
        try {
            res = resReqApi(req, req42ApiLocationBeginUri(i));
        } catch (RuntimeException e) {
            System.out.println("==== begin error ====");
            e.printStackTrace();
        }
        return clusterMapping(res.getBody());
    }

    // me 정보 반환
    @Retryable(maxAttempts = 10, backoff = @Backoff(1000))
    @Async("apiTaskExecutor")
    public CompletableFuture<Seoul42> getMeInfo(String token) {
        req = req42ApiHeader(aes.decoding(token));
        res = resReqApi(req, req42MeUri());
        return CompletableFuture.completedFuture(seoul42Mapping(res.getBody()));
    }

    // 검색 시 10명 단위의 Seoul42를 반환해주는 메소드
    @Retryable(maxAttempts = 10, backoff = @Backoff(1000))
    @Async("apiTaskExecutor")
    public CompletableFuture<List<Seoul42>> get42UsersInfoInRange(String token, String begin, String end) {
        req = req42ApiHeader(aes.decoding(token));
        res = resReqApi(req, req42ApiUsersInRangeUri(begin, end));
        return CompletableFuture.completedFuture(seoul42ListMapping(res.getBody()));
    }

    @Recover
    public CompletableFuture<Seoul42> fallback(RuntimeException e, String token) {
        System.out.println("==== api error ====");
        e.printStackTrace();
        throw new RegisteredFriendException();
    }

    public <T> T injectInfo(CompletableFuture<T> info) {
        T ret = null;
        try {
            ret = info.get();
        } catch (CancellationException | InterruptedException | ExecutionException e) {
            System.out.println("==== inject error ====");
            e.printStackTrace();
            throw new DuplicateGroupNameException();
        }
        return ret;
    }

    // 한 유저에 대해 하네 정보를 추가해주는 메소드
    public Planet getHaneInfo(String name, String token) {
        req = reqHaneApiHeader(token);
        res = resReqApi(req, reqHaneApiUri(name));
        Hane hane = haneMapping(res.getBody());
        if (hane.getInoutState().equalsIgnoreCase("IN")) {
            if (hane.getCluster().equalsIgnoreCase("GAEPO"))
                return Planet.gaepo;
            return Planet.seocho;
        }
        return null;
    }

    // 관리자 헤더
    public HttpEntity<MultiValueMap<String, String>> req42AdminHeader(String code) {
        headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        params = new LinkedMultiValueMap<>();
        params.add("grant_type","authorization_code");
        params.add("client_id","");
        params.add("client_secret", "");
        params.add("code", code);
        params.add("redirect_uri","");
        return new HttpEntity<>(params, headers);
    }

    public HttpEntity<MultiValueMap<String, String>> req42AdminRefreshHeader(String refreshToken) {
        headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("client_id", "");
        params.add("client_secret", "");
        params.add("refresh_token", refreshToken);
        return new HttpEntity<>(params, headers);
    }

    public HttpEntity<MultiValueMap<String, String>> req42TokenHeader(String code) {
        headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        params = new LinkedMultiValueMap<>();
        params.add("grant_type","authorization_code");
        params.add("client_id","");
        params.add("client_secret", "");
        params.add("code", code);
        params.add("redirect_uri","");
        return new HttpEntity<>(params, headers);
    }

    public HttpEntity<MultiValueMap<String, String>> req42RefreshHeader(String refreshToken) {
        headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("client_id", "");
        params.add("client_secret", "");
        params.add("refresh_token", refreshToken);
        return new HttpEntity<>(params, headers);
    }

    // 42api 요청 헤더 생성 메소드
    public HttpEntity<MultiValueMap<String, String>> req42ApiHeader(String token) {
        headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        headers.add("Content-type", "application/json;charset=utf-8");
        params = new LinkedMultiValueMap<>();
        return new HttpEntity<>(params, headers);
    }

    // hane 요청 헤더 생성 메소드
    public HttpEntity<MultiValueMap<String, String>> reqHaneApiHeader(String token) {
        headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        headers.add("Content-type", "application/json;charset=utf-8");
        params = new LinkedMultiValueMap<>();
        return new HttpEntity<>(params, headers);
    }

    public URI req42TokenUri() {
        return UriComponentsBuilder.fromHttpUrl("https://api.intra.42.fr/oauth/token")
                .build()
                .toUri();
    }

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

    public URI req42ApiLocationUri(int i) {
        return UriComponentsBuilder.newInstance()
                .scheme("https").host("api.intra.42.fr").path(Define.INTRA_VERSION_PATH + "/campus/" + Define.SEOUL + "/locations")
                .queryParam("page[size]", 100)
                .queryParam("page[number]", i)
                .queryParam("sort", "-end_at")
                .build()
                .toUri();
    }

    public URI req42ApiLocationEndUri(int i) {
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = new Date();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, -3);
        return UriComponentsBuilder.newInstance()
                .scheme("https").host("api.intra.42.fr").path(Define.INTRA_VERSION_PATH + "/campus/" + Define.SEOUL + "/locations")
                .queryParam("page[size]", 100)
                .queryParam("page[number]", i)
                .queryParam("range[end_at]", sdf.format(cal.getTime()) + "," + sdf.format(date))
                .build()
                .toUri();
    }

    public URI req42ApiLocationBeginUri(int i) {
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = new Date();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, -3);
        return UriComponentsBuilder.newInstance()
                .scheme("https").host("api.intra.42.fr").path(Define.INTRA_VERSION_PATH + "/campus/" + Define.SEOUL + "/locations")
                .queryParam("page[size]", 50)
                .queryParam("page[number]", i)
                .queryParam("range[begin_at]", sdf.format(cal.getTime()) + "," + sdf.format(date))
                .build()
                .toUri();
    }

    public URI req42MeUri() {
        return UriComponentsBuilder.newInstance()
                .scheme("https").host("api.intra.42.fr").path(Define.INTRA_VERSION_PATH + "/me")
                .build()
                .toUri();
    }

    // 유저 범위 설정 검색 요청 uri 생성 메소드
    public URI req42ApiUsersInRangeUri(String begin, String end) {
        return UriComponentsBuilder.newInstance()
                .scheme("https").host("api.intra.42.fr").path(Define.INTRA_VERSION_PATH + "/campus/" + Define.SEOUL + "/users")
                .queryParam("sort", "login")
                .queryParam("range[login]", begin + "," + end)
                .queryParam("page[size]", "10")
                .build()
                .toUri();
    }

    // 한 유저에 대한 하네 정보 요청 uri 생성 메소드
    public URI reqHaneApiUri(String name) {
        return UriComponentsBuilder.fromHttpUrl("" + name)
                .build()
                .toUri();
    }

    public OAuthToken oAuthTokenMapping(String body) {
        OAuthToken oAuthToken = null;
        try {
            oAuthToken = om.readValue(body, OAuthToken.class);
        } catch (JsonProcessingException e) {
            throw new JsonDeserializeException();
        }
        return oAuthToken;
    }

    public Seoul42 seoul42Mapping(String body) {
        Seoul42 seoul42 = null;
        try {
            seoul42 = om.readValue(body, Seoul42.class);
        } catch (JsonProcessingException e) {
            throw new JsonDeserializeException();
        }
        return seoul42;
    }

    public List<Cluster> clusterMapping(String body) {
        List<Cluster> clusters = null;
        try {
            clusters = Arrays.asList(om.readValue(body, Cluster[].class));
        } catch (JsonProcessingException e) {
            throw new JsonDeserializeException();
        }
        return clusters;
    }

    public List<Seoul42> seoul42ListMapping(String body) {
        List<Seoul42> seoul42List = null;
        try {
            seoul42List = Arrays.asList(om.readValue(body, Seoul42[].class));
        } catch (JsonProcessingException e) {
            throw new JsonDeserializeException();
        }
        return seoul42List;
    }

    public Hane haneMapping(String body) {
        Hane hane = null;
        try {
            hane = om.readValue(body, Hane.class);
        } catch (JsonProcessingException e) {
            throw new JsonDeserializeException();
        }
        return hane;
    }

    // api req요청에 대한 응답 반환 메소드
    public ResponseEntity<String> resReqApi(HttpEntity<MultiValueMap<String, String>> req, URI url) {
        return rt.exchange(
                url.toString(),
                HttpMethod.GET,
                req,
                String.class);
    }

    // api post요청에 대한 응답 반환 메소드
    public ResponseEntity<String> resPostApi(HttpEntity<MultiValueMap<String, String>> req, URI url) {
        return rt.exchange(
                url.toString(),
                HttpMethod.POST,
                req,
                String.class);
    }
}
