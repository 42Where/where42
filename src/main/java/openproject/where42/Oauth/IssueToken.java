package openproject.where42.Oauth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class IssueToken {

	@GetMapping("/invalidRefreshToken")
	public String invalidRefreshToken(@CookieValue("refresh_token") String refreshToken, HttpServletResponse response) {

		/*** refresh call ***/
		RestTemplate rt = new RestTemplate();
		HttpHeaders codeHeaders = new HttpHeaders();
		codeHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type","refresh_token");
		params.add("client_id","u-s4t2ud-6d1e73793782a2c15be3c0d2d507e679adeed16e50deafcdb85af92e91c30bd0");
		params.add("client_secret", "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");
		params.add("refresh_token", refreshToken);

		HttpEntity<MultiValueMap<String, String>> authTokenRequest =
				new HttpEntity<>(params, codeHeaders);

		ResponseEntity<String> response2 = rt.exchange(
				"https://api.intra.42.fr/oauth/token",
				HttpMethod.POST,
				authTokenRequest,
				String.class
		);
		System.out.println(response2.getBody());

		/*** parsing ***/
		ObjectMapper objectMapper = new ObjectMapper();
		OAuthToken oauthToken = null;
		try {
			oauthToken = objectMapper.readValue(response2.getBody(), OAuthToken.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		/*** save cookie ***/
		Cookie cookie = new Cookie("access_code", oauthToken.getAccess_token());
		cookie.setPath("/");
		cookie.setMaxAge(7200);
		response.addCookie(cookie);

		return "/auth/logins";
	}
}
