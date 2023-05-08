package openproject.where42.token;

import lombok.RequiredArgsConstructor;
import openproject.where42.api.ApiService;
import openproject.where42.api.mapper.OAuthToken;
import openproject.where42.api.mapper.Seoul42;
import openproject.where42.exception.customException.TokenExpiredException;
import openproject.where42.exception.customException.UnregisteredMemberException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.concurrent.CompletableFuture;


/**
 * 토큰 관련 서비스
 * @version 1.0
 * @see  openproject.where42.token
 */
@Service
@RequiredArgsConstructor
public class TokenService {
	private final TokenRepository tokenRepository;
	private final ApiService apiService;
	static private AES aes = new AES();
	static private MakeCookie oven = new MakeCookie();

	/**
	 * <pre>
	 * 새로 42 토큰을 발급받기 위한 함수.
	 * DB에서 기존에 발급받았던 토큰을 확인.
	 * 만약 받았다면 DB 업데이트 진행 후 쿠키 생성
	 * 처음 받는다면 DB에 새로 등록 진행
	 * </pre>
	 * @author sunghkim
	 * @since 1.0
	 * @param response 쿠키 생성 및 갱신용 Http 응답 객체
	 * @param code 42 토큰을 발급받기 위한 OAuth code값
	 * @return 발급받은 토큰으로 가져온 사용자에 대한 기본 정보가 담긴 객체
	 * @see openproject.where42.api.ApiService#getOAuthToken(String, String)
	 * @see openproject.where42.api.ApiService#getMeInfo(String)
	 * @see openproject.where42.token.AES#encoding(String)
	 * @see openproject.where42.token.TokenRepository#findTokenByKey(String)
	 * @see openproject.where42.token.TokenRepository#updateRefreshToken(Token, String)
	 * @see openproject.where42.token.TokenRepository#updateAccessToken(Token, String)
	 * @see openproject.where42.token.TokenRepository#saveRefreshToken(String, OAuthToken)
	 */
	public Seoul42 beginningIssue(HttpServletResponse response, String code) {
		OAuthToken oAuthToken = apiService.getOAuthToken(tokenRepository.callSecret(), code);;
		Seoul42 seoul42 = apiService.getMeInfo(aes.encoding(oAuthToken.getAccess_token()));
		System.out.println("oauth token = " + oAuthToken.getAccess_token());
		Token token = tokenRepository.findTokenByName(seoul42.getLogin());
		if (token != null){ // 만약 이미 DB에 Token들이 저장된 흔적이 있으면 업데이트만 해줌
			tokenRepository.updateRefreshToken(token, oAuthToken.getRefresh_token());
			addCookie(response, tokenRepository.updateAccessToken(token, oAuthToken.getAccess_token()));
			return seoul42;
		}
		addCookie(response, tokenRepository.saveRefreshToken(seoul42.getLogin(), oAuthToken));
		return seoul42;
	}

	public void addCookie(HttpServletResponse rep,String key) {
		rep.addCookie(oven.bakingMaxAge("1209600", 1209600));
		rep.addCookie(oven.bakingCookie("ID", key, 1209600));
	}

	/**
	 * <pre>
	 * 기존에 사용하던 토큰을 갱신하기 위한 함수.
	 * Refresh 토큰을 사용하여 Access 토큰을 새로 발급
	 * 발급 받은 뒤 DB에 토큰 업데이트
	 * </pre>
	 * @author sunghkim
	 * @since 1.0
	 * @param rep 쿠키 생성 및 갱신용 Http 응답 객체
	 * @param key DB에서 토큰을 찾기 위한 고유 key
	 * @return 암호화된 새로 발급받은 토큰
	 * @see openproject.where42.token.AES#encoding(String)
	 * @see openproject.where42.token.TokenRepository#findTokenByKey(String)
	 * @see openproject.where42.api.ApiService#getOAuthToken(String, String)
	 * @see openproject.where42.token.TokenRepository#updateRefreshToken(Token, String)
	 * @see openproject.where42.token.TokenRepository#updateAccessToken(Token, String)
	 */
	public String issueAccessToken(HttpServletResponse rep ,String key) {
		Token token = tokenRepository.findTokenByKey(key);
		OAuthToken oAuthToken = apiService.getNewOAuthToken(tokenRepository.callSecret(), aes.decoding(token.getRefreshToken()));
		tokenRepository.updateRefreshToken(token,oAuthToken.getRefresh_token());
		tokenRepository.updateAccessToken(token,oAuthToken.getAccess_token());
		addCookie(rep, key);
		return aes.encoding(oAuthToken.getAccess_token());
	}

	/**
	 * <pre>
	 * Access 토큰을 조회하는 함수
	 * 토큰을 찾기 위해 필요한 key가 없다면 예외처리
	 * key는 있으나 유효하지 않거나 토큰이 없다면 예외처리
	 * 토큰이 발급받은 시간이 1시간 50분이 지났다면 새로 토큰을 발급
	 * 위에 모두 해당사항 없이 정상적이라면 토큰을 반환
	 * </pre>
	 * @author sunghkim
	 * @since 1.0
	 * @param rep 쿠키 생성 및 갱신용 Http 응답 객체
	 * @param key DB에서 토큰을 찾기 위한 고유 key
	 * @return 기존에 DB에 있던 토큰
	 * @see openproject.where42.token.TokenRepository#findTokenByKey(String)
	 * @see openproject.where42.token.TokenService#issueAccessToken(HttpServletResponse, String) 
	 * @throws TokenExpiredException()
	 */
	public String findAccessToken(HttpServletResponse rep, String key) {
		if (key == null)
			throw new TokenExpiredException();
		Token token = tokenRepository.findTokenByKey(key);
		if (token == null){
			rep.addCookie(oven.burnCookie("ID"));
			rep.addCookie(oven.bakingMaxAge("0", 0));
			throw new TokenExpiredException();
		}
		Date now = new Date();
		if ((now.getTime() - token.getRecentLogin().getTime()) / 60000 > 110){
			addCookie(rep, key);
			return issueAccessToken(rep, key);
		}
		return token.getAccessToken();
	}
}
