package openproject.where42.Oauth;

import lombok.RequiredArgsConstructor;
import openproject.where42.Oauth.domain.Token;
import openproject.where42.api.ApiService;
import openproject.where42.cookie.AES;
import openproject.where42.cookie.MakeCookie;
import openproject.where42.exception.CookieExpiredException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenService {
	private final TokenRepository tokenRepository;
	private final ApiService apiService;
	static private AES aes = new AES();
	static private MakeCookie oven = new MakeCookie();

	public void checkRefreshToken(String key) {
		if (key == null || tokenRepository.checkRefreshToken(key) == false)
			throw new CookieExpiredException();
	}

	public void addCookie(HttpServletResponse rep, String token42,String key) {
		rep.addCookie(oven.bakingCookie("access_token", token42, 7200));
		rep.addCookie(oven.bakingMaxAge("1209600", 1209600));
		rep.addCookie(oven.bakingCookie("ID", key, 1209600));
	}

	/*** Access Token 새로 발급***/
	public String issueAccessToken(String key) {
		System.out.println("======= invalidRefreshToken call =======");

		Token token = tokenRepository.findTokenByKey(key);
		/*** refresh call ***/
		ResponseEntity<String> response = apiService.resPostApi(
				apiService.callRefreshHttp(aes.decoding(token.getToken())),
				apiService.req42TokenUri()
		);

		/*** parsing ***/
		OAuthToken oauthToken = apiService.oAuthTokenMapping(response.getBody());
		tokenRepository.updateTokenByKey(token,oauthToken.getRefresh_token());

		return aes.encoding(oauthToken.getAccess_token());
	}

	public void inspectToken(HttpServletResponse rep, String key) {
		checkRefreshToken(key);
		addCookie(rep, issueAccessToken(key),key);
	}

	public List<String> BeginnigIssue(String code) {
		List<String> result = new ArrayList<>();
		OAuthToken oAuthToken = apiService.getOauthToken(code);
		result.add(tokenRepository.saveRefreshToken(aes.encoding(oAuthToken.getRefresh_token())));
		result.add(oAuthToken.getAccess_token());
		return result;
	}
}
