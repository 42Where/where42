package openproject.where42.token;

import lombok.RequiredArgsConstructor;
import openproject.where42.api.dto.OAuthToken;
import openproject.where42.member.MemberRepository;
import openproject.where42.token.entity.Token;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TokenRepository {

	static private AES aes = new AES();
	private final EntityManager em;
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

	/*** token 업데이트 ***/
	@Transactional
	public void updateRefreshToken(Token token, String value) {
		token.updateRefresh(aes.encoding(value));
	}

	@Transactional
	public String updateAccessToken(Token token, String value) { return token.updateAccess(aes.encoding(value)); }
}
