package openproject.where42.token;

import lombok.RequiredArgsConstructor;
import openproject.where42.api.ApiService;
import openproject.where42.api.dto.OAuthToken;
import openproject.where42.api.dto.Seoul42;
import openproject.where42.exception.customException.CookieExpiredException;
import openproject.where42.member.MemberService;
import openproject.where42.token.entity.Token;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class TokenService {
	private final MemberService memberService;
	private final TokenRepository tokenRepository;
	private final ApiService apiService;
	static private AES aes = new AES();
	static private MakeCookie oven = new MakeCookie();

	public Seoul42 beginningIssue(HttpServletResponse response, String code) {
		CompletableFuture<OAuthToken> cf1 = apiService.getOAuthToken(code);;
		OAuthToken oAuthToken = apiService.injectInfo(cf1);
		CompletableFuture<Seoul42> cf2 = apiService.getMeInfo(aes.encoding(oAuthToken.getAccess_token()));
		Seoul42 seoul42 = apiService.injectInfo(cf2);
		Token token = tokenRepository.findTokenByName(seoul42.getLogin());
		if (token != null){ // 만약 이미 DB에 Token들이 저장된 흔적이 있으면 업데이트만 해줌
			tokenRepository.updateRefreshToken(token, oAuthToken.getRefresh_token());
			addCookie(response, tokenRepository.updateAccessToken(token, oAuthToken.getAccess_token()));
			return seoul42;
		}
		addCookie(response, tokenRepository.saveRefreshToken(seoul42.getLogin(), oAuthToken));
		return seoul42;
	}

	public void checkRefreshToken(String key) {
		if (key == null || !tokenRepository.checkRefreshToken(key))
			throw new CookieExpiredException();
	}

	public void addCookie(HttpServletResponse rep,String key) {
		rep.addCookie(oven.bakingMaxAge("1209600", 1209600));
		rep.addCookie(oven.bakingCookie("ID", key, 1209600));
	}

	/*** Access Token 새로 발급***/
	public String issueAccessToken(String key) {
		Token token = tokenRepository.findTokenByKey(key);
		CompletableFuture<OAuthToken> cf = apiService.getNewOAuthToken(aes.decoding(token.getRefreshToken()));
		OAuthToken oAuthToken = apiService.injectInfo(cf);
		tokenRepository.updateRefreshToken(token,oAuthToken.getRefresh_token());
		return aes.encoding(oAuthToken.getAccess_token());
	}

	public void inspectToken(HttpServletResponse res, String key) throws CookieExpiredException {
		checkRefreshToken(key);
		addCookie(res, key);
	}

	public String findAccessToken(String key) {
		Token token = tokenRepository.findTokenByKey(key);
		if (token == null || token.getAccessToken() == null)
			return null;
		return token.getAccessToken();
	}
}
