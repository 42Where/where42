package openproject.where42.check;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import openproject.where42.member.OAuthToken;
import openproject.where42.member.Seoul42;
import openproject.where42.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Getter
@NoArgsConstructor
public class CheckApi {

	private String access_token;
	private String token_type;
	private String refresh_token;
	private int expires_in;
	private String scope;
	private int created_at;

	public void setting(String code) {
		RestTemplate rt = new RestTemplate(); //http 요청을 간단하게 해줄 수 있는 클래스
		//HttpHeader 오브젝트 생성
		HttpHeaders codeHeaders = new HttpHeaders();
		codeHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		//HttpBody 오브젝트 생성
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type","authorization_code");
		params.add("client_id","150e45a44fb1c8b17fe04470bdf8fabd56c1b9841d2fa951aadb4345f03008fe");
		params.add("client_secret", "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");
		params.add("code", code);
		params.add("redirect_uri","http://localhost:8080/auth/login/callback");

		//HttpHeader와 HttpBody를 하나의 오브젝트에 담기
		HttpEntity<MultiValueMap<String, String>> authTokenRequest =
				new HttpEntity<>(params, codeHeaders);

		//실제로 요청하기
		//Http 요청하기 - POST 방식으로 - 그리고 response 변수의 응답을 받음.
		ResponseEntity<String> response = rt.exchange(
				"https://api.intra.42.fr/oauth/token",
				HttpMethod.POST,
				authTokenRequest,
				String.class
		);

		//Gson Library, JSON SIMPLE LIBRARY, OBJECT MAPPER(Check)
		ObjectMapper objectMapper = new ObjectMapper();
		OAuthToken oauthToken = null;
		//Model과 다르게 되있으면 그리고 getter setter가 없으면 오류가 날 것이다.
		try {
			oauthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		this.access_token = oauthToken.getAccess_token();
		this.token_type = oauthToken.getToken_type();
		this.refresh_token = oauthToken.getRefresh_token();
		this.expires_in = oauthToken.getExpires_in();
		this.created_at = oauthToken.getCreated_at();
		this.scope = oauthToken.getScope();
	}
	public ResponseEntity<String> callMeInfo() {
		RestTemplate rt = new RestTemplate();
		HttpHeaders tokenHeaders = new HttpHeaders();
		tokenHeaders.add("Authorization", "Bearer " + this.access_token);
		tokenHeaders.add("Content-type", "application/json;charset=utf-8");
		MultiValueMap<String, String> params2 = new LinkedMultiValueMap<>();
		HttpEntity<MultiValueMap<String, String>> request =
				new HttpEntity<>(params2, tokenHeaders);

		// HTTP 요청할 떄 생성한 Header 설정
		//        ResponseEntity<String> responseEntity = restTemplate.exchange("요청 URL"
		//                , HttpMethod.GET, new HttpEntity<>(headers), String.class);
		URI url = UriComponentsBuilder.fromHttpUrl("https://api.intra.42.fr/v2/me")
				.build()
				.toUri();

		return rt.exchange(
				url.toString(),
				HttpMethod.GET,
				request,
				String.class);
	}

	public ResponseEntity<String> callNameInfo(String name) {
		RestTemplate rt = new RestTemplate();
		HttpHeaders tokenHeaders = new HttpHeaders();
		tokenHeaders.add("Authorization", "Bearer " + this.access_token);
		tokenHeaders.add("Content-type", "application/json;charset=utf-8");
		MultiValueMap<String, String> params2 = new LinkedMultiValueMap<>();
		HttpEntity<MultiValueMap<String, String>> request =
				new HttpEntity<>(params2, tokenHeaders);

		// HTTP 요청할 떄 생성한 Header 설정
//                ResponseEntity<String> responseEntity = restTemplate.exchange("요청 URL"
//                        , HttpMethod.GET, new HttpEntity<>(headers), String.class);
		URI url = UriComponentsBuilder.fromHttpUrl("https://api.intra.42.fr/v2/users/" + name)
				.build()
				.toUri();

		return rt.exchange(
				url.toString(),
				HttpMethod.GET,
				request,
				String.class);
	}

	public Seoul42 check42Api(String name) {
		ObjectMapper objectMapper = new ObjectMapper();
		Seoul42 seoul42 = null;

		ResponseEntity<String> response = callNameInfo(name);
		try {
			seoul42 = objectMapper.readValue(response.getBody(), Seoul42.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return seoul42;
	}
}
