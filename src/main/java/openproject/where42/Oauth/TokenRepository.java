package openproject.where42.Oauth;

import lombok.RequiredArgsConstructor;
import openproject.where42.Oauth.domain.Token;
import openproject.where42.cookie.AES;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TokenRepository {

	private final AES aes;
	private final EntityManager em;
	@Transactional
	public String saveRefreshToken(String value) {
		Token token = new Token(value);
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
	@Transactional
	/*** token 업데이트 ***/
	public void updateTokenByKey(Token token, String value) {
		token.updateValue(aes.encoding(value));
	}
}
