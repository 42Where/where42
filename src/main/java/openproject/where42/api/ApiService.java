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
    private final AES aes = new AES();
    private final ObjectMapper om = new ObjectMapper();
    private final RestTemplate rt = new RestTemplate();
    public final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.of("UTC")));
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    HttpHeaders headers;
    HttpEntity<MultiValueMap<String, String>> req;
    MultiValueMap<String, String> params;
    ResponseEntity<String> res;

    // oAuth 토큰 반환
    @Retryable(maxAttempts = 3, backoff = @Backoff(1000))
    public OAuthToken getOAuthToken(String secret, String code) {
        req = req42TokenHeader(secret, code);
        try {
            res = resPostApi(req, req42TokenUri());
        } catch (RuntimeException e) {
            e.getMessage();
        }
        return oAuthTokenMapping(res.getBody());
    }

    // oAuth 토큰 반환
    @Retryable(maxAttempts = 3, backoff = @Backoff(1000))
    public OAuthToken getNewOAuthToken(String secret, String token) {
        req = req42RefreshHeader(secret, token);
        try {
            res = resPostApi(req, req42TokenUri());
        } catch (RuntimeException e) {
            e.getMessage();
        }
        return oAuthTokenMapping(res.getBody());
    }

    @Retryable(maxAttempts = 3, backoff = @Backoff(1000))
    public OAuthToken getAdminOAuthToken(String secret, String code) {
        req = req42AdminHeader(secret, code);
        System.out.println("getAdmin 111111111111111");
        res = resPostApi(req, req42TokenUri());
        System.out.println("getAdmin 222222222222222");
        return oAuthTokenMapping(res.getBody());
    }

    @Retryable(maxAttempts = 3, backoff = @Backoff(1000))
    public OAuthToken getAdminNewOAuthToken(String secret, String token) {
        req = req42AdminRefreshHeader(secret, token);
        res = resPostApi(req, req42TokenUri());
        return oAuthTokenMapping(res.getBody());
    }

    // 이미지 호출
    @Retryable(maxAttempts = 3, backoff = @Backoff(1000))
    public List<Seoul42> get42Image(String token, int i) {
        req = req42ApiHeader(token);
        res = resReqApi(req, req42ApiImageUri(i));
        return seoul42ListMapping(res.getBody());
    }

    // 현재 클러스터에 있는 카뎃들 호출
    @Retryable(maxAttempts = 3, backoff = @Backoff(1000))
    public List<Cluster> get42ClusterInfo(String token, int i) {
        req = req42ApiHeader(token);
        res = resReqApi(req, req42ApiLocationUri(i));
        return clusterMapping(res.getBody());
    }

    // 로그아웃한 카뎃들 호출
    @Retryable(maxAttempts = 3, backoff = @Backoff(1000))
    public List<Cluster> get42LocationEnd(String token, int i) {
        req = req42ApiHeader(token);
        try {
            res = resReqApi(req, req42ApiLocationEndUri(i));
        } catch (RuntimeException e) {
            e.getMessage(); // log 자리
        }
        return clusterMapping(res.getBody());
    }

    // 로그인한 카뎃들 호출
    @Retryable(maxAttempts = 3, backoff = @Backoff(1000))
    public List<Cluster> get42LocationBegin(String token, int i) {
        req = req42ApiHeader(token);
        try {
            res = resReqApi(req, req42ApiLocationBeginUri(i));
        } catch (RuntimeException e) {
            e.getMessage(); // log 자리
        }
        return clusterMapping(res.getBody());
    }

    // me 정보 반환
    @Retryable(maxAttempts = 3, backoff = @Backoff(1000))
    @Async("apiTaskExecutor")
    public Seoul42 getMeInfo(String token) {
        req = req42ApiHeader(aes.decoding(token));
        res = resReqApi(req, req42MeUri());
        return seoul42Mapping(res.getBody());
    }

    @Retryable(maxAttempts = 3, backoff = @Backoff(1000))
    @Async("apiTaskExecutor")
    public Seoul42 getUserInfo(String name, String token) {
        req = req42ApiHeader(token);
        res = resReqApi(req, req42UserUri(name));
        return seoul42Mapping(res.getBody());
    }

    // 검색 시 10명 단위의 Seoul42를 반환해주는 메소드
    @Retryable(maxAttempts = 3, backoff = @Backoff(1000))
    @Async("apiTaskExecutor")
    public List<Seoul42> get42UsersInfoInRange(String token, String begin, String end) {
        req = req42ApiHeader(aes.decoding(token));
        res = resReqApi(req, req42ApiUsersInRangeUri(begin, end));
        return seoul42ListMapping(res.getBody());
    }

    @Recover
    public Seoul42 fallback(RuntimeException e, String token) {
        e.getMessage(); // log 자리
        throw new TooManyRequestException();
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

    // 헤더
    public HttpEntity<MultiValueMap<String, String>> req42TokenHeader(String secret, String code) {
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

    public HttpEntity<MultiValueMap<String, String>> req42RefreshHeader(String secret, String refreshToken) {
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
        cal.add(Calendar.MINUTE, -5);
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
        cal.add(Calendar.MINUTE, -5);
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