package openproject.where42.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import openproject.where42.api.dto.*;
import openproject.where42.exception.customException.JsonDeserializeException;
import openproject.where42.exception.customException.TooManyRequestException;
import openproject.where42.token.AES;
import openproject.where42.member.entity.enums.Planet;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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

@Service // 이컨트롤러 쓴느게 맞나..ㅎ 다시 생각.. 왜 스프링 빈에 등록이 안된다는걸까??
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
    @Retry(name = "backend")
    @Async("apiTaskExecutor")
    public CompletableFuture<OAuthToken> getOAuthToken(String code) {
        req = req42TokenHeader(code);
        res = resPostApi(req, req42TokenUri());
        return CompletableFuture.completedFuture(oAuthTokenMapping(res.getBody()));
    }

    // oAuth 토큰 반환
    @Retry(name = "backend")
    @Async("apiTaskExecutor")
    public CompletableFuture<OAuthToken> getNewOAuthToken(String token) {
        req = req42RefreshHeader(token);
        res = resPostApi(req, req42TokenUri());
        return CompletableFuture.completedFuture(oAuthTokenMapping(res.getBody()));
    }

    // me 정보 반환
    @Retry(name = "backend")
    @Async("apiTaskExecutor")
    public CompletableFuture<Seoul42> getMeInfo(String token) {
        req = req42ApiHeader(aes.decoding(token));
        res = resReqApi(req, req42MeUri());
        return CompletableFuture.completedFuture(seoul42Mapping(res.getBody()));
    }

    // 검색 시 10명 단위의 Seoul42를 반환해주는 메소드
    // 검색 시 Location 및 img가 나오지 않아 seoul42 -> searchCadet으로 변환해야함
    @Retry(name = "backend")
    @Async("apiTaskExecutor")
    public CompletableFuture<List<Seoul42>> get42UsersInfoInRange(String token, String begin, String end) {
        req = req42ApiHeader(aes.decoding(token));
        res = resReqApi(req, req42ApiUsersInRangeUri(begin, end));
        return CompletableFuture.completedFuture(seoul42ListMapping(res.getBody()));
    }

    //테스트용
//    @Retry(name = "backend")
//    @Async("apiTaskExecutor")
//    public CompletableFuture<List<Cluster>> get42ClusterInfo(String token, int i) {
//        req = req42ApiHeader(aes.decoding(token));
//        res = resReqApi(req, req42ApiLocationUri(i));
//        return CompletableFuture.completedFuture(occupyingMapping(res.getBody()));
//    }

    @Retry(name = "backend")
    @Async("apiTaskExecutor")
    public CompletableFuture<List<Cluster>> get42ClusterInfo(String token, int i) {
        req = req42ApiHeader(token);
        res = resReqApi(req, req42ApiLocationUri(i));
        return CompletableFuture.completedFuture(occupyingMapping(res.getBody()));
    }

    @Retry(name = "backend")
    @Async("apiTaskExecutor")
    public CompletableFuture<List<Cluster>> get42LocationEnd(String token, int i) {
        req = req42ApiHeader(token);
        res = resReqApi(req, req42ApiLocationEndUri(i));
        return CompletableFuture.completedFuture(occupyingMapping(res.getBody()));
    }

    @Retry(name = "backend")
    @Async("apiTaskExecutor")
    public CompletableFuture<List<Cluster>> get42LocationBegin(String token, int i) {
        req = req42ApiHeader(token);
        res = resReqApi(req, req42ApiLocationBeginUri(i));
        return CompletableFuture.completedFuture(occupyingMapping(res.getBody()));
    }

    // 유저 한명에 대해 img, location 정보만 반환해주는 메소드
    @Retry(name = "backend")
    @Async("apiTaskExecutor")
    public CompletableFuture<Seoul42> get42ShortInfo(String token, String name) {
        req = req42ApiHeader(aes.decoding(token));
        res = resReqApi(req, req42ApiOneUserUri(name));
        return CompletableFuture.completedFuture(seoul42Mapping(res.getBody()));
    }

    @Retry(name = "backend")
    @Async("apiThreadPoolTaskExecutor")
    public CompletableFuture<SearchCadet> get42DetailInfo(String token, String name) {
        req = req42ApiHeader(aes.decoding(token));
        res = resReqApi(req, req42ApiOneUserUri(name));
        return CompletableFuture.completedFuture(searchCadetMapping(res.getBody()));
    }

    public <T> T injectInfo(CompletableFuture<T> info) {
        T ret = null;
        try {
            ret = info.get();
        } catch (CancellationException | InterruptedException | ExecutionException e) {
            throw new TooManyRequestException();
        }
        return ret;
    }

    // 한 유저에 대해 하네 정보를 추가해주는 메소드 (hane true/false 로직으로 변경 가능한지 고민, 외출 등을 살릴 경우 hane 매핑하는 객체를 아예 따로 만드는게 나을지도?)
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

    /*** 관리자용 ***/
    public HttpEntity<MultiValueMap<String, String>> req42AdminHeader(String code) {
        headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        params = new LinkedMultiValueMap<>();
        params.add("grant_type","authorization_code");
        params.add("client_id","56448d39501e3f2a4d1c574a72de267e8def4da40b4b98fa29bce33063e1feff");
        params.add("client_secret", "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");
        params.add("code", code);
        params.add("redirect_uri","http://localhost:8080/admin");
        return new HttpEntity<>(params, headers);
    }

    public HttpEntity<MultiValueMap<String, String>> req42AdminRefreshHeader(String refreshToken) {
        headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("client_id", "56448d39501e3f2a4d1c574a72de267e8def4da40b4b98fa29bce33063e1feff");
        params.add("client_secret", "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");
        params.add("refresh_token", refreshToken);
        return new HttpEntity<>(params, headers);
    }

    /*** 로컬용 ***/
//    public HttpEntity<MultiValueMap<String, String>> req42TokenHeader(String code) {
//        headers = new HttpHeaders();
//        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
//        params = new LinkedMultiValueMap<>();
//        params.add("grant_type","authorization_code");
//        params.add("client_id","150e45a44fb1c8b17fe04470bdf8fabd56c1b9841d2fa951aadb4345f03008fe");
//        params.add("client_secret", "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");
//        params.add("code", code);
//        params.add("redirect_uri","http://localhost:8080/auth/login/callback");
//        return new HttpEntity<>(params, headers);
//    }
//
//    public HttpEntity<MultiValueMap<String, String>> req42RefreshHeader(String refreshToken) {
//        headers = new HttpHeaders();
//        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
//        params = new LinkedMultiValueMap<>();
//        params.add("grant_type", "refresh_token");
//        params.add("client_id", "150e45a44fb1c8b17fe04470bdf8fabd56c1b9841d2fa951aadb4345f03008fe");
//        params.add("client_secret", "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");
//        params.add("refresh_token", refreshToken);
//        return new HttpEntity<>(params, headers);
//    }

    /*** 서버용 ***/
    public HttpEntity<MultiValueMap<String, String>> req42TokenHeader(String code) {
        headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        params = new LinkedMultiValueMap<>();
        params.add("grant_type","authorization_code");
        params.add("client_id","u-s4t2ud-6d1e73793782a2c15be3c0d2d507e679adeed16e50deafcdb85af92e91c30bd0");
        params.add("client_secret", "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");
        params.add("code", code);
        params.add("redirect_uri","http://www.where42.kr/auth/login/callback");
        return new HttpEntity<>(params, headers);
    }

    public HttpEntity<MultiValueMap<String, String>> req42RefreshHeader(String refreshToken) {
        headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("client_id", "u-s4t2ud-6d1e73793782a2c15be3c0d2d507e679adeed16e50deafcdb85af92e91c30bd0");
        params.add("client_secret", "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");
        params.add("refresh_token", refreshToken);
        return new HttpEntity<>(params, headers);
    }

    // 42api 요청 헤더 생성 메소드
    public HttpEntity<MultiValueMap<String, String>> req42ApiHeader(String token) {
        headers = new HttpHeaders(); // 새 헤더를 만들지 않으면 429에러가 바로 난다.
        headers.add("Authorization", "Bearer " + token);
        headers.add("Content-type", "application/json;charset=utf-8");
        params = new LinkedMultiValueMap<>(); // 얘네는 new 처리 하는게 맞겠지..?
        return new HttpEntity<>(params, headers); // 헤더 공통으로 쓸 수 있는 방법
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

    public URI req42MeUri() {
        return UriComponentsBuilder.newInstance()
                .scheme("https").host("api.intra.42.fr").path(Define.INTRA_VERSION_PATH + "/me")
                .build()
                .toUri();
    }

    public URI req42ApiLocationUri(int i) {
        return UriComponentsBuilder.newInstance() // /v2/campus/:campus_id/locations
                .scheme("https").host("api.intra.42.fr").path(Define.INTRA_VERSION_PATH + "/campus/" + Define.SEOUL + "/locations")
                .queryParam("page[size]", 50)
                .queryParam("page[number]", i)
                .queryParam("sort", "-end_at")
//                .queryParam("range[end_at]", null) // 널만 검색하는 게 분명히 있을텐데요./.
                .build()
                .toUri();
    }

    public URI req42ApiLocationEndUri(int i) {
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = new Date();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, -3);
        return UriComponentsBuilder.newInstance() // /v2/campus/:campus_id/locations
                .scheme("https").host("api.intra.42.fr").path(Define.INTRA_VERSION_PATH + "/campus/" + Define.SEOUL + "/locations")
                .queryParam("page[size]", 50)
                .queryParam("page[number]", i)
                .queryParam("range[end_at]", sdf.format(cal.getTime()) + "," + sdf.format(date)) // 널만 검색하는 게 분명히 있을텐데요./.
                .build()
                .toUri();
    }

    public URI req42ApiLocationBeginUri(int i) {
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = new Date();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, -3);
        return UriComponentsBuilder.newInstance() // /v2/campus/:campus_id/locations
                .scheme("https").host("api.intra.42.fr").path(Define.INTRA_VERSION_PATH + "/campus/" + Define.SEOUL + "/locations")
                .queryParam("page[size]", 50)
                .queryParam("page[number]", i)
                .queryParam("range[begin_at]", sdf.format(cal.getTime()) + "," + sdf.format(date)) // 널만 검색하는 게 분명히 있을텐데요./.
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

    // 한 유저에 대한 me 정보 요청 uri 생성 메소드
    public URI req42ApiOneUserUri(String name) {
        return UriComponentsBuilder.newInstance()
                .scheme("https").host("api.intra.42.fr").path(Define.INTRA_VERSION_PATH + "/users/" + name)
                .build()
                .toUri();
    }

    // 한 유저에 대한 하네 정보 요청 uri 생성 메소드
    public URI reqHaneApiUri(String name) {
        return UriComponentsBuilder.fromHttpUrl("https://api.24hoursarenotenough.42seoul.kr/ext/where42/where42/" + name)
                .build()
                .toUri();
    }

    // oAuth 객체 json 매핑 메소드
    public OAuthToken oAuthTokenMapping(String body) {
        OAuthToken oAuthToken = null;
        try {
            oAuthToken = om.readValue(body, OAuthToken.class);
        } catch (JsonProcessingException e) {
            throw new JsonDeserializeException();
        }
        return oAuthToken;
    }

    // seoul42 객체 json 매핑 메소드
    public Seoul42 seoul42Mapping(String body) {
        Seoul42 seoul42 = null;
        try {
            seoul42 = om.readValue(body, Seoul42.class);
        } catch (JsonProcessingException e) {
            throw new JsonDeserializeException();
        }
        return seoul42;
    }

    public List<Cluster> occupyingMapping(String body) {
        List<Cluster> clusters = null;
        try {
            clusters = Arrays.asList(om.readValue(body, Cluster[].class));
        } catch (JsonProcessingException e) {
            throw new JsonDeserializeException();
        }
        return clusters;
    }

    // ListSeoul42 객체 json 매핑 메소드
    public List<Seoul42> seoul42ListMapping(String body) {
        List<Seoul42> seoul42List = null;
        try {
            seoul42List = Arrays.asList(om.readValue(body, Seoul42[].class));
        } catch (JsonProcessingException e) {
            throw new JsonDeserializeException();
        }
        return seoul42List;
    }

    // searchCadet 객체 json 매핑 메소드
    public SearchCadet searchCadetMapping(String body) {
        SearchCadet cadet = null;
        try {
            cadet = om.readValue(body, SearchCadet.class);
        } catch (JsonProcessingException e) {
            throw new JsonDeserializeException();
        }
        return cadet;
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
//        if(bucket.tryConsume(1)){
//            System.out.println("index : " + i);
//        }else{
//            System.out.println("Token is Empty!");
//            //Toekn 충전
//            sleep(10000);
//        }
//        try {
        return rt.exchange(
                url.toString(),
                HttpMethod.GET,
                req,
                String.class);
//        }
//        catch (HttpClientErrorException.TooManyRequests e) {
//            throw new TooManyRequestException();
//        }
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
