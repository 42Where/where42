package openproject.where42.token;

import lombok.RequiredArgsConstructor;
import openproject.where42.api.ApiService;
import openproject.where42.api.mapper.OAuthToken;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Date;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TokenRepository {

	static private AES aes = new AES();
	private final EntityManager em;
	private final ApiService apiService;

	@Transactional
	public String saveAdmin(String name , OAuthToken oAuthToken) {
		Token token = new Token(
				name,
				oAuthToken.getAccess_token(),
				oAuthToken.getRefresh_token());
		em.persist(token);
		return token.getUUID();
	}
	@Transactional
	public String saveRefreshToken(String name , OAuthToken oAuthToken) {
		Token token = new Token(
				name,
				aes.encoding(oAuthToken.getAccess_token()),
				aes.encoding(oAuthToken.getRefresh_token()));
		em.persist(token);
		return token.getUUID();
	}

	/*** token이 있는지 없는지 확인하는 함수 ***/
	public Boolean checkRefreshToken(String key) {
		try {
			em.createQuery("select m from Token m where m.UUID = :key", Token.class)
					.setParameter("key", key)
					.getSingleResult();
			return true;
		} catch (NoResultException e) {
			return false;
		}
	}
	/*** token을 찾아서 반환 ***/
	public Token findTokenByKey(String key) {
		try {
			return em.createQuery("select m from Token m where m.UUID = :key", Token.class)
					.setParameter("key", key)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public Token findTokenByName(String name) {
		try {
			return em.createQuery("select m from Token m where m.memberName = :name", Token.class)
					.setParameter("name", name)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@Transactional
	public void checkToken(Token token) {
		Date now = new Date();
		Long diff = (now.getTime() - token.getRecentLogin().getTime()) / 60000;
		System.out.println("시간이 얼마나 흘렀나요 == " + diff);
		if (diff > 60){
			System.out.println("어드민 토큰을 바꿀 시간");
			System.out.println("지금 토큰 == " + token.getAccessToken());
			OAuthToken oAuthToken = apiService.getAdminNewOAuthToken(token.getRefreshToken());
			token.updateAccess(oAuthToken.getAccess_token());
			token.updateRefresh(oAuthToken.getRefresh_token());
			System.out.println("바뀐 토큰 == " + token.getAccessToken());
		}
	}
	@Transactional
	public String callAdmin() {
		try {
			Token token =  em.createQuery("select m from Token m where m.memberName = :name", Token.class)
					.setParameter("name", "admin")
					.getSingleResult();
			checkToken(token);
			return token.getAccessToken();
		} catch (NoResultException e) {
			return null;
		}
	}

	public String callHane() {
		try {
			Token token = em.createQuery("select m from Token m where m.memberName = :name", Token.class)
					.setParameter("name", "hane")
					.getSingleResult();
			return token.getAccessToken();
		} catch (NoResultException e) {
			return null;
		}
	}

	/*** token 업데이트 ***/
	@Transactional
	public void updateRefreshToken(Token token, String value) {
		token.updateRefresh(aes.encoding(value));
	}

	@Transactional
	public String updateAccessToken(Token token, String value) { return token.updateAccess(aes.encoding(value)); }

	@Transactional
	public void insertHane() {
		Token hane = new Token("hane",
				"",
				null);
		em.persist(hane);
	}
}
