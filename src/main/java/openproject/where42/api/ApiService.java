package openproject.where42.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import openproject.where42.api.dto.SearchCadet;
import openproject.where42.api.dto.Seoul42;
import openproject.where42.check.AES;
import openproject.where42.member.domain.Member;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

@Service // 이컨트롤러 쓴느게 맞나..ㅎ 다시 생각.. 왜 스프링 빈에 등록이 안된다는걸까??
public class ApiService {
    private static final AES aes = new AES();
    private static final ObjectMapper om = new ObjectMapper();
    private static final RestTemplate rt = new RestTemplate();
    HttpEntity<MultiValueMap<String, String>> req;
    ResponseEntity<String> res;

    // 검색 시 10명 단위의 Seoul42를 반환해주는 메소드
    // 검색 시 Location 및 img가 나오지 않아 seoul42 -> searchCadet으로 변환해야함
    public List<Seoul42> get42UsersInfoInRange(String token, String begin, String end) {
        req = req42ApiHeader(aes.decoding(token));
        res = resApi(req, req42ApiUsersInRangeUri(begin, end));
        return seoul42ListMapping(res.getBody());
    }

    // 유저 한명에 대해 img, location 정보만 반환해주는 메소드
    public Seoul42 get42ShortInfo(String token, String name) {
        System.out.println(aes.decoding(token));
        req = req42ApiHeader(aes.decoding(token));
        res = resApi(req, req42ApiOneUserUri(name));
        return seoul42Mapping(res.getBody());
    }

    // 유저 한명에 대해 모든 정보를 반환해주는 메소드
    public SearchCadet get42DetailInfo(String token, Seoul42 cadet) {
        req = req42ApiHeader(aes.decoding(token));
        res = resApi(req, req42ApiOneUserUri(cadet.getLogin()));
        return searchCadetMapping(res.getBody());
    }

    // 한 유저에 대해 하네 정보를 추가해주는 메소드 (hane true/false 로직으로 변경 가능한지 고민, 외출 등을 살릴 경우 hane 매핑하는 객체를 아예 따로 만드는게 나을지도?)
    public int getHaneInfo(String token, String name) {
//        req = reqHaneApiHeader(aes.decoding(token));
//        res = resApi(req, reqHaneApiUri(cadet.getLogin()));
//        if (res.getBody().equalsIgnoreCase("in"))
//            return 1; // 외출 로직 확인
//        return 0;
        return Define.IN;
    }

    // 42api 요청 헤더 생성 메소드
    public HttpEntity<MultiValueMap<String, String>> req42ApiHeader(String token) {
        HttpHeaders headers = new HttpHeaders(); // 새 헤더를 만들지 않으면 429에러가 바로 난다.
        headers.add("Authorization", "Bearer " + token);
        headers.add("Content-type", "application/json;charset=utf-8");
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>(); // 얘네는 new 처리 하는게 맞겠지..?
        return new HttpEntity<>(params, headers); // 헤더 공통으로 쓸 수 있는 방법
    }

    // hane 요청 헤더 생성 메소드
    public HttpEntity<MultiValueMap<String, String>> req42HaneHeader(String token) {
//        HttpHeaders headers = new HttpHeaders(); // 새 헤더를 만들지 않으면 429에러가 바로 난다.
//        headers.add("Authorization", "Bearer " + token);
//        headers.add("Content-type", "application/json;charset=utf-8");
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>(); // 얘네는 new 처리 하는게 맞겠지..?
//        return new HttpEntity<>(params, headers); // 헤더 공통으로 쓸 수 있는 방법
        return null;
    }

    // 유저 범위 설정 검색 요청 uri 생성 메소드
    public URI req42ApiUsersInRangeUri(String begin, String end) {
        return UriComponentsBuilder.newInstance()
                .scheme("https").host("api.intra.42.fr").path("/v2/campus/" + Define.SEOUL + "/users/")
                .queryParam("sort", "login")
                .queryParam("range[login]", begin + "," + end)
                .queryParam("page[size]", "10")
                .build()
                .toUri();
    }

    // 한 유저에 대한 me 정보 요청 uri 생성 메소드
    public URI req42ApiOneUserUri(String name) {
        return UriComponentsBuilder.newInstance()
                .scheme("https").host("api.intra.42.fr").path("/v2/users/" + name)
                .build()
                .toUri();
    }

    // 한 유저에 대한 하네 정보 요청 uri 생성 메소드

    public URI reqHaneApiUri(String login) {
        return UriComponentsBuilder.newInstance()
                .scheme("https").host("24hoursarenotenough.42seoul.kr").path("/v1/tag-log/maininfo" + login)
                .build()
                .toUri();
    }

    // seoul42 객체 json 매핑 메소드
    public Seoul42 seoul42Mapping(String body) {
        Seoul42 seoul42 = null;
        try {
            seoul42 = om.readValue(body, Seoul42.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return seoul42;
    }

    // ListSeoul42 객체 json 매핑 메소드

    public List<Seoul42> seoul42ListMapping(String body) {
        List<Seoul42> seoul42List = null;
        try {
            seoul42List = Arrays.asList(om.readValue(body, Seoul42[].class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return seoul42List;
    }

    // searchCadet 객체 json 매핑 메소드
    public SearchCadet searchCadetMapping(String body) {
        SearchCadet cadet = null;
        try {
            cadet = om.readValue(body, SearchCadet.class); // hane꺼는 써치카뎃으로 하지말고 string으로 inourStatus만 가져와서 true/false로 반환하는 방법 고민
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return cadet;
    }

    // api 요청에 대한 응답 반환 메소드

    public ResponseEntity<String> resApi(HttpEntity<MultiValueMap<String, String>> req, URI url) {
        return rt.exchange(
                url.toString(),
                HttpMethod.GET,
                req,
                String.class);
    }
}
