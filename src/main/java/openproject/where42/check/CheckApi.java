package openproject.where42.check;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import openproject.where42.api.ApiService;
import openproject.where42.api.dto.Seoul42;
import openproject.where42.Oauth.OAuthToken;
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
	private HttpHeaders tokenHeaders = new HttpHeaders();
	static private RestTemplate rt = new RestTemplate();
	static private ApiService apiService = new ApiService();

	// Refresh Token용 header body 생성기
	public HttpEntity<MultiValueMap<String, String>> callRefreshHttp(String refreshToken) {
		/*** Header 생성 ***/
		HttpHeaders codeHeaders = new HttpHeaders();
		codeHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		/*** body 생성 ***/
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type","refresh_token");
		params.add("client_id","u-s4t2ud-6d1e73793782a2c15be3c0d2d507e679adeed16e50deafcdb85af92e91c30bd0");
		params.add("client_secret", "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");
		params.add("refresh_token", refreshToken);

		return new HttpEntity<>(params, codeHeaders);
	}

	// Access Token용 header body 생성기
	public HttpEntity<MultiValueMap<String, String>> callAccessHttp(String code) {

		/*** Header 생성 ***/
		HttpHeaders codeHeaders = new HttpHeaders();
		codeHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		/*** body 생성 ***/
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type","authorization_code");
		params.add("client_id","u-s4t2ud-6d1e73793782a2c15be3c0d2d507e679adeed16e50deafcdb85af92e91c30bd0");
		params.add("client_secret", "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");
		params.add("code", code);
		params.add("redirect_uri","http://localhost:8080/auth/login/callback");

		return new HttpEntity<>(params, codeHeaders); // 반환
	}

	//정보 주입
	private void injectInfo(OAuthToken oAuthToken) {
		this.access_token = oAuthToken.getAccess_token();
		this.token_type = oAuthToken.getToken_type();
		this.refresh_token = oAuthToken.getRefresh_token();
		this.expires_in = oAuthToken.getExpires_in();
		this.created_at = oAuthToken.getCreated_at();
		this.scope = oAuthToken.getScope();
		tokenHeaders.add("Authorization", "Bearer " + this.access_token);
		tokenHeaders.add("Content-type", "application/json;charset=utf-8");
	}

	public void setting(String code) {

		ResponseEntity<String> response = rt.exchange(
				"https://api.intra.42.fr/oauth/token",
				HttpMethod.POST,
				callAccessHttp(code),
				String.class);

		ObjectMapper objectMapper = new ObjectMapper();
		OAuthToken oauthToken = null;
		try {
			oauthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		injectInfo(oauthToken);
	}

	public ResponseEntity<String> callMeInfo() {
		MultiValueMap<String, String> params2 = new LinkedMultiValueMap<>();

		HttpEntity<MultiValueMap<String, String>> request =
				new HttpEntity<>(params2, tokenHeaders);

		URI url = UriComponentsBuilder.fromHttpUrl("https://api.intra.42.fr/v2/me")
				.build()
				.toUri();
		return apiService.resApi(request, url);
	}

	public ResponseEntity<String> callNameInfo(String name) {
		MultiValueMap<String, String> params2 = new LinkedMultiValueMap<>();
		HttpEntity<MultiValueMap<String, String>> request =
				new HttpEntity<>(params2, tokenHeaders);

		return apiService.resApi(request, apiService.req42ApiOneUserUri(name));
	}

	public Seoul42 check42Api(String name) {
		ResponseEntity<String> response = callNameInfo(name);
		return apiService.seoul42Mapping(response.getBody());
	}
}
