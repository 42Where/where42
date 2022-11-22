package openproject.where42.token;

import lombok.RequiredArgsConstructor;
import openproject.where42.api.ApiService;
import openproject.where42.api.dto.OAuthToken;
import openproject.where42.exception.customException.CookieExpiredException;
import openproject.where42.token.entity.Token;
import org.springframework.stereotype.Service;

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

	public List<String> beginningIssue(String code) {
		List<String> result = new ArrayList<>();
		OAuthToken oAuthToken = apiService.getOAuthToken(code);
		result.add(tokenRepository.saveRefreshToken(aes.encoding(oAuthToken.getRefresh_token())));
		result.add(oAuthToken.getAccess_token());
		return result;
	}

	public void checkRefreshToken(String key) {
		if (key == null || !tokenRepository.checkRefreshToken(key))
			throw new CookieExpiredException();
	}

	public void addCookie(HttpServletResponse rep, String token42,String key) {
		rep.addCookie(oven.bakingCookie("access_token", token42, 7200));
		rep.addCookie(oven.bakingMaxAge("1209600", 1209600));
		rep.addCookie(oven.bakingCookie("ID", key, 1209600));
	}

	/*** Access Token 새로 발급***/
	public String issueAccessToken(String key) {
		System.out.println("======= invalidRefreshToken call ======="); // 이건 무엇?

		Token token = tokenRepository.findTokenByKey(key);
		OAuthToken oAuthToken = apiService.getNewOAuthToken(aes.decoding(token.getToken()));
		tokenRepository.updateTokenByKey(token,oAuthToken.getRefresh_token());
		return aes.encoding(oAuthToken.getAccess_token());
	}

	public void inspectToken(HttpServletResponse res, String key) {
		checkRefreshToken(key);
		addCookie(res, issueAccessToken(key),key);
	}
}
