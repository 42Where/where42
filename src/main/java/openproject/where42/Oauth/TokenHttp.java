package openproject.where42.Oauth;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

public class TokenHttp {

	static private RestTemplate rt = new RestTemplate();

	public HttpEntity<MultiValueMap<String, String>> callRefreshHttp(String refreshToken) {
		/*** Header 생성 ***/
		HttpHeaders codeHeaders = new HttpHeaders();
		codeHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		/*** body 생성 ***/
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "refresh_token");
		params.add("client_id", "u-s4t2ud-6d1e73793782a2c15be3c0d2d507e679adeed16e50deafcdb85af92e91c30bd0");
		params.add("client_secret", "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");
		params.add("refresh_token", refreshToken);

		return new HttpEntity<>(params, codeHeaders);
	}

	public URI createUrl(String url) {
		return UriComponentsBuilder.fromHttpUrl(url)
				.build()
				.toUri();
	}
}
