package openproject.where42.api;

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

@Service // 이컨트롤러 쓴느게 맞나..ㅎ 다시 생각..
public class ApiService {
    RestTemplate rt = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();

    public HttpEntity<MultiValueMap<String, String>> req42ApiHeader(String token) { // cache 처리 혹은 객체 처리
        this.headers.add("Authorization", "Bearer " + token); // token을 여기서 유저에 대한 캐시로 받거나.. 뭐 그럴 수 있으면 좋을듯
        this.headers.add("Content-type", "application/json;charset=utf-8");
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>(); // 얘네는 new 처리 하는게 맞겠지..?
        return new HttpEntity<>(params, this.headers); // 헤더 공통으로 쓸 수 있는 방법
    }

    public URI req42ApiSearchUsersUri(String begin, String end) {
        return UriComponentsBuilder.newInstance()
                .scheme("https").host("api.intra.42.fr").path("/v2/campus/29/users/")
                .queryParam("sort", "login")
                .queryParam("range[login]", begin + "," + end)
                .queryParam("page[size]", "10") // 100개이상 어떻게 해야하는 지 모르겠음.. 그게 끝인지 알 방법이.. 어떻게 다시 호출하지 거기부터..?
                .build()
                .toUri();
    }

    public URI req42ApiSearchOneUserUri(String login) {
       return UriComponentsBuilder.newInstance()
                .scheme("https").host("api.intra.42.fr").path("/v2/users/" + login)
                .build()
                .toUri();
    }

    public URI reqHaneApiUri(String login) {
        return UriComponentsBuilder.newInstance()
                .scheme("https").host("24hoursarenotenough.42seoul.kr").path("/v1/tag-log/maininfo" + login)
                .build()
                .toUri();
    }

    public ResponseEntity<String> resApi(HttpEntity<MultiValueMap<String, String>> req, URI url) {
        return this.rt.exchange(
                url.toString(),
                HttpMethod.GET,
                req,
                String.class);
    }
}
