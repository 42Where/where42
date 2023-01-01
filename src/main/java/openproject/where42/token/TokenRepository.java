package openproject.where42.token;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openproject.where42.api.ApiService;
import openproject.where42.api.mapper.OAuthToken;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Date;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class TokenRepository {

	static private AES aes = new AES();
	private final EntityManager em;
	private final ApiService apiService;

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
		log.info("[checkToken] Token을 발급받은지 {}시간 지났습니다.", diff);
		if (diff > 60){
			log.info("[checkToken] 시간이 경과하여 새로운 Token을 발급받습니다\n ========= 기존 토큰 =========\n{}", token.getAccessToken());
			OAuthToken oAuthToken = apiService.getNewOAuthToken();
			token.updateAccess(oAuthToken.getAccess_token());
			token.updateRefresh(oAuthToken.getRefresh_token());
			log.info("[checkToken] ========= 발급받은 토큰 =========\n{}", token.getAccessToken());
		}
	}

	public String callSecret(){
		try {
			Token token =  em.createQuery("select m from Token m where m.memberName = :name", Token.class)
					.setParameter("name", "secret")
					.getSingleResult();
			return token.getAccessToken();
		} catch (NoResultException e) {
			return null;
		}
	}
	public String call() {
		try {
			Token token =  em.createQuery("select m from Token m where m.memberName = :name", Token.class)
					.setParameter("name", "")
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
	public void insertHane(String token) {
		try {
			Token hane = em.createQuery("select t from Token t where t.memberName= :name", Token.class)
					.setParameter("name", "hane")
					.getSingleResult();
			hane.updateAccess(token);
		} catch (NoResultException e) {
			Token hane = new Token("hane", token, "null");
			em.persist(hane);
		}
	}

	@Transactional
	public void insertSecret(String secret) {
		try {
			Token tmp = em.createQuery("select t from Token t where t.memberName= :name", Token.class)
					.setParameter("name", "secret")
					.getSingleResult();
			tmp.updateAccess(secret);
		} catch (NoResultException e) {
			Token tmp = new Token("secret", secret, "null");
			em.persist(tmp);
		}
	}
}
