package openproject.where42.Oauth;

import lombok.RequiredArgsConstructor;
import openproject.where42.Oauth.domain.Token;
import openproject.where42.api.ApiService;
import openproject.where42.cookie.AES;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenService {
	private final TokenRepository tokenRepository;
	private final ApiService apiService;
	static private TokenHttp tokenHttp = new TokenHttp();
	static private AES aes = new AES();

	public boolean checkRefreshToken(String key) {
		return tokenRepository.checkRefreshToken(key);
	}

	/*** Access Token 새로 발급***/
	public String issueAccessToken(String key) {
		System.out.println("======= invalidRefreshToken call =======");

		Token token = tokenRepository.findTokenByKey(key);
		/*** refresh call ***/
		ResponseEntity<String> response = apiService.resPostApi(
				tokenHttp.callRefreshHttp(aes.decoding(token.getToken())),
				apiService.req42TokenUri()
		);

		/*** parsing ***/
		OAuthToken oauthToken = apiService.oAuthTokenMapping(response.getBody());
		tokenRepository.updateTokenByKey(token,oauthToken.getRefresh_token());

//		/*** save cookie ***/ ==> 로직 변경
//		response.addCookie(oven.bakingCookie("access_token", aes.encoding(oauthToken.getAccess_token()), 7200));
//		response.addCookie(oven.bakingCookie("refresh_token", aes.encoding(oauthToken.getRefresh_token()), 1209600));
//		response.addCookie(oven.bakingMaxAge("1209600", 1209600));
		return aes.encoding(oauthToken.getAccess_token());
	}

	public String issueAllToken(String code, String key){
		Token token = tokenRepository.findTokenByKey(key);
		OAuthToken oAuthToken = apiService.getOauthToken(code);
		tokenRepository.updateTokenByKey(token, oAuthToken.getRefresh_token());
		return oAuthToken.getAccess_token();
	}

	public List<String> BeginnigIssue(String code) {
		List<String> result = new ArrayList<>();
		OAuthToken oAuthToken = apiService.getOauthToken(code);
		result.add(tokenRepository.saveRefreshToken(code));
		result.add(oAuthToken.getAccess_token());
		return result;
	}
}
