package openproject.where42.Oauth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import openproject.where42.api.ApiService;
import openproject.where42.api.dto.Seoul42;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Getter
@NoArgsConstructor
public class TokenDao {
	static private TokenHttp tokenHttp = new TokenHttp();
	static private ApiService apiService = new ApiService();

	public OAuthToken getAllToken(String code) {

		ObjectMapper objectMapper = new ObjectMapper();
		OAuthToken oauthToken = null;
		try {
			oauthToken = objectMapper.readValue(
					tokenHttp.resPostApi(tokenHttp.callAccessHttp(code),
					tokenHttp.createUrl("https://api.intra.42.fr/oauth/token")).getBody(),
					OAuthToken.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return oauthToken;
	}
	private HttpHeaders makeHeader(String access_token) {
		HttpHeaders tokenHeaders = new HttpHeaders();
		tokenHeaders.add("Authorization", "Bearer " + access_token);
		tokenHeaders.add("Content-type", "application/json;charset=utf-8");
		return tokenHeaders;
	}
	public ResponseEntity<String> callMeInfo(String access_token) {
		HttpHeaders tokenHeaders = makeHeader(access_token);
		MultiValueMap<String, String> params2 = new LinkedMultiValueMap<>();
		HttpEntity<MultiValueMap<String, String>> request =
				new HttpEntity<>(params2, tokenHeaders);

		return apiService.resApi(request, tokenHttp.createUrl("https://api.intra.42.fr/v2/me"));
	}

	public ResponseEntity<String> callNameInfo(String name, String access_token) {
		HttpHeaders tokenHeaders = makeHeader(access_token);
		MultiValueMap<String, String> params2 = new LinkedMultiValueMap<>();
		HttpEntity<MultiValueMap<String, String>> request =
				new HttpEntity<>(params2, tokenHeaders);

		return apiService.resApi(request, apiService.req42ApiOneUserUri(name));
	}

//	public Seoul42 call42Api(String name) {
//		ResponseEntity<String> response = callNameInfo(name);
//		return apiService.seoul42Mapping(response.getBody());
//	}
}
