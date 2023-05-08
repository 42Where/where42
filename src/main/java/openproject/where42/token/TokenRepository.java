package openproject.where42.token;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openproject.where42.admin.AdminRepository;
import openproject.where42.api.mapper.OAuthToken;
import openproject.where42.admin.AdminService;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Date;


/**
 * 토큰 DB를 조회하는 레포지토리
 * @version 1.0
 * @see  openproject.where42.token
 */
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class TokenRepository {

	static private AES aes = new AES();
	private final EntityManager em;
	private final AdminService adminService;
	private final AdminRepository adminRepository;

	/**
	 * <pre>
	 *  사용자의 토큰을 저장하는 함수
	 * </pre>
	 * @author sunghkim
	 * @since 1.0
	 * @param name 저장할 사용자 ID
	 * @param oAuthToken 저장할 토큰
	 * @return 저장한 뒤 key 값이 될 uuid 반환
	 */
	@Transactional
	public String saveRefreshToken(String name , OAuthToken oAuthToken) {
		Token token = new Token(
				name,
				aes.encoding(oAuthToken.getAccess_token()),
				aes.encoding(oAuthToken.getRefresh_token()));
		em.persist(token);
		return token.getUUID();
	}

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

	/**
	 * <pre>
	 *  key값으로 토큰을 찾아서 반환하는 함수
	 * </pre>
	 * @author sunghkim
	 * @since 1.0
	 * @param key 찾으려는 토큰의 uuid(key)값
	 * @return 토큰이 존재하면 Token 객체를 반환, 없다면 null 반환
	 */
	public Token findTokenByKey(String key) {
		try {
			return em.createQuery("select m from Token m where m.UUID = :key", Token.class)
					.setParameter("key", key)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	/**
	 * <pre>
	 *  intra 아이디(name) 값으로 토큰을 찾아서 반환하는 함수
	 * </pre>
	 * @author sunghkim
	 * @since 1.0
	 * @param name 찾으려는 토큰의 소유주 intra 아이디(name)
	 * @return 토큰이 존재하면 Token 객체를 반환, 없다면 null 반환
	 */
	public Token findTokenByName(String name) {
		try {
			return em.createQuery("select m from Token m where m.memberName = :name", Token.class)
					.setParameter("name", name)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
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

	/*** token 업데이트 ***/
	@Transactional
	public void updateRefreshToken(Token token, String value) {
		token.updateRefresh(aes.encoding(value));
	}

	@Transactional
	public String updateAccessToken(Token token, String value) { return token.updateAccess(aes.encoding(value)); }

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
